package eu.semagrow.hibiscus.config;

import eu.semagrow.core.config.SemagrowSchema;
import eu.semagrow.core.config.SourceSelectorConfigException;
import eu.semagrow.core.config.SourceSelectorImplConfigBase;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;

/**
 * Created by angel on 16/6/2015.
 */
public class QuetsalSourceSelectorImplConfig extends SourceSelectorImplConfigBase
{
    private String summariesFile;
    private String metadataFile;
    private String mode;
    private double commonPredicateThreshold;

    public QuetsalSourceSelectorImplConfig(String type) { super(type); }

    public String getSummariesFile() {
        return summariesFile;
    }

    public void setSummariesFile(String summariesFile) {
        this.summariesFile = summariesFile;
    }

    public String getMetadataFile() {
        return metadataFile;
    }

    public void setMetadataFile(String metadataFile) {
        this.metadataFile = metadataFile;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public double getCommonPredicateThreshold() {
        return commonPredicateThreshold;
    }

    public void setCommonPredicateThreshold(double commonPredicateThreshold) {
        this.commonPredicateThreshold = commonPredicateThreshold;
    }


    @Override
    public Resource export(Model graph) {
        Resource node = super.export(graph);
        ValueFactory vf = SimpleValueFactory.getInstance();

        if (mode != null)
            graph.add(node, QuetsalSchema.MODE, vf.createLiteral(mode));


        graph.add(node, QuetsalSchema.COMMONPREDTHREASHOLD, vf.createLiteral(commonPredicateThreshold));

        if (summariesFile != null)
            graph.add(node, QuetsalSchema.SUMMARIES, vf.createLiteral(summariesFile));

        return node;
    }

    @Override
    public void parse(Model graph, Resource resource)
            throws SourceSelectorConfigException
    {

        Literal summariesLit = Models.objectLiteral(graph.filter(resource, QuetsalSchema.SUMMARIES, null)).get();
        if (summariesLit != null) {
            summariesFile = summariesLit.getLabel();
        }

        Literal metadataLit = Models.objectLiteral(graph.filter(resource, SemagrowSchema.METADATAINIT,null)).get();
        if (metadataLit != null) {
            metadataFile = metadataLit.getLabel();
        }

        Literal modeLit = Models.objectLiteral(graph.filter(resource, QuetsalSchema.MODE,null)).get();

        if (modeLit != null) {
            mode = modeLit.getLabel();
        }

        Literal commonPredLit = Models.objectLiteral(graph.filter(resource, QuetsalSchema.COMMONPREDTHREASHOLD,null)).get();

        if (commonPredLit != null) {
            commonPredicateThreshold = commonPredLit.doubleValue();
        }


    }
}
