package eu.semagrow.hibiscus.selector;

import com.fluidops.fedx.EndpointManager;
import com.fluidops.fedx.FedX;
import com.fluidops.fedx.FederationManager;
import com.fluidops.fedx.algebra.StatementSource;
import com.fluidops.fedx.cache.Cache;
import com.fluidops.fedx.structures.Endpoint;
import eu.semagrow.core.source.*;
import eu.semagrow.hibiscus.util.BasicGraphPatternExtractor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.algebra.QueryRoot;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by angel on 26/6/2015.
 */
public class QuetsalSourceSelector {

    protected Cache cache;
    protected List<Endpoint> members;

    public QuetsalSourceSelector() {

        cache = FederationManager.getInstance().getCache();
        FedX fed = FederationManager.getInstance().getFederation();
        members = fed.getMembers();
    }

    protected List<SourceMetadata> toSourceMetadata(Map<org.openrdf.query.algebra.StatementPattern, List<StatementSource>> lst)
    {
        List<SourceMetadata> metadata = new LinkedList<>();

        SiteRegistry registry = SiteRegistry.getInstance();
        SiteFactory factory = registry.get("SPARQL").get();

        for (org.openrdf.query.algebra.StatementPattern pattern : lst.keySet()) {
            List<StatementSource> sources = lst.get(pattern);
            if (!sources.isEmpty()) {
                for (StatementSource src : sources) {
                    IRI endpoint = toURI(src);
                    metadata.add(new SourceMetadata() {
                        @Override
                        public List<Site> getSites() {
                            //FIXME
                            SiteConfig config = factory.getConfig();
                            config.parse(null, endpoint);
                            return Collections.singletonList(factory.getSite(config));
                        }

                        @Override
                        public StatementPattern original() {
                            return openrdf2rdf4j(pattern);
                        }

                        @Override
                        public StatementPattern target() {
                            return openrdf2rdf4j(pattern);
                        }

                        @Override
                        public Collection<IRI> getSchema(String var) {
                            return null;
                        }

                        @Override
                        public boolean isTransformed() {
                            return false;
                        }

                        @Override
                        public double getSemanticProximity() {
                            return 0;
                        }
                    });
                }
            }
        }
        return metadata;
    }

    private IRI toURI(StatementSource src)
    {
        String endpointId = src.getEndpointID();

        return SimpleValueFactory.getInstance().createIRI(
                EndpointManager.getEndpointManager().getEndpoint(endpointId).getEndpoint() );
    }

    protected HashMap<Integer, List<org.openrdf.query.algebra.StatementPattern>> generateBgpGroups(TupleExpr expr) {

        HashMap<Integer, List<org.openrdf.query.algebra.StatementPattern>> bgpGrps = new HashMap<Integer, List<org.openrdf.query.algebra.StatementPattern>>();
        int grpNo = 0;

        TupleExpr e = expr.clone();

        List<TupleExpr> bgps = BasicGraphPatternExtractor.process(e);

        if (bgps.isEmpty())
            bgps = Collections.singletonList(new QueryRoot(e));

        for (TupleExpr bgp : bgps) {
            List<StatementPattern> patterns = StatementPatternCollector.process(bgp);
            bgpGrps.put(grpNo, patterns.stream().map((StatementPattern x) -> rdf4j2openrdf(x)).collect(Collectors.toList()));
            grpNo++;
        }
        return bgpGrps;
    }

    org.openrdf.query.algebra.StatementPattern rdf4j2openrdf(org.eclipse.rdf4j.query.algebra.StatementPattern pattern) {
        return new org.openrdf.query.algebra.StatementPattern(rdf4j2openrdf(pattern.getSubjectVar()), rdf4j2openrdf(pattern.getPredicateVar()), rdf4j2openrdf(pattern.getObjectVar()));
    }

    org.eclipse.rdf4j.query.algebra.StatementPattern openrdf2rdf4j(org.openrdf.query.algebra.StatementPattern pattern) {
        return new org.eclipse.rdf4j.query.algebra.StatementPattern(openrdf2rdf4j(pattern.getSubjectVar()), openrdf2rdf4j(pattern.getPredicateVar()), openrdf2rdf4j(pattern.getObjectVar()));
    }

    org.openrdf.query.algebra.Var rdf4j2openrdf(org.eclipse.rdf4j.query.algebra.Var v) {
            if (v.hasValue())
                return new org.openrdf.query.algebra.Var(v.getName(), rdf4jopenrdf(v.getValue()));
            else
                return new org.openrdf.query.algebra.Var(v.getName());
    }

    org.eclipse.rdf4j.query.algebra.Var openrdf2rdf4j(org.openrdf.query.algebra.Var v) {
        if (v.hasValue())
            return new org.eclipse.rdf4j.query.algebra.Var(v.getName(), openrdf2rdf4j(v.getValue()));
        else
            return new org.eclipse.rdf4j.query.algebra.Var(v.getName());
    }

    org.openrdf.model.Value rdf4jopenrdf(org.eclipse.rdf4j.model.Value v) {
        org.openrdf.model.ValueFactory vf = ValueFactoryImpl.getInstance();
        if (v instanceof org.eclipse.rdf4j.model.IRI)
            return vf.createURI(((org.eclipse.rdf4j.model.IRI)v).stringValue());
        else if (v instanceof org.eclipse.rdf4j.model.BNode)
            return vf.createBNode(((org.eclipse.rdf4j.model.BNode)v).stringValue());
        else if (v instanceof org.eclipse.rdf4j.model.Literal)
            return vf.createLiteral(((org.eclipse.rdf4j.model.Literal)v).getLabel());
        return null;
    }

    org.eclipse.rdf4j.model.Value openrdf2rdf4j(org.openrdf.model.Value v) {
        org.eclipse.rdf4j.model.ValueFactory vf = SimpleValueFactory.getInstance();
        if (v instanceof org.openrdf.model.URI)
            return vf.createIRI(((org.openrdf.model.URI)v).stringValue());
        else if (v instanceof org.openrdf.model.BNode)
            return vf.createBNode(((org.openrdf.model.BNode)v).stringValue());
        else if (v instanceof org.openrdf.model.Literal)
            return vf.createLiteral(((org.openrdf.model.Literal)v).getLabel());
        return null;
    }
}
