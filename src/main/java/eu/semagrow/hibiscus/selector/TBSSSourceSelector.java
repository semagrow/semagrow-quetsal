package eu.semagrow.hibiscus.selector;

import eu.semagrow.core.source.SourceMetadata;
import eu.semagrow.core.source.SourceSelector;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.aksw.simba.quetzal.core.TBSSSourceSelection;

import java.util.*;

/**
 * Created by angel on 24/6/2015.
 */
public class TBSSSourceSelector extends QuetsalSourceSelector implements SourceSelector {

    private TBSSSourceSelection impl;

    public TBSSSourceSelector() { super(); }

    @Override
    public List<SourceMetadata> getSources(StatementPattern pattern, Dataset dataset, BindingSet bindings) {
        return null;
    }

    @Override
    public List<SourceMetadata> getSources(Iterable<StatementPattern> patterns, Dataset dataset, BindingSet bindings) {
        return null;
    }

    @Override
    public List<SourceMetadata> getSources(TupleExpr expr, Dataset dataset, BindingSet bindings)
    {
        String query = null;
        try {
            this.impl = new TBSSSourceSelection(members, cache, query);
            HashMap<Integer, List<org.openrdf.query.algebra.StatementPattern>> bgpGrps =  generateBgpGroups(expr);
            return toSourceMetadata(this.impl.performSourceSelection(bgpGrps));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
