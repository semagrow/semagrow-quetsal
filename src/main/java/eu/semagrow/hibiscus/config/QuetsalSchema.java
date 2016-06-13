package eu.semagrow.hibiscus.config;

import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * Created by angel on 16/6/2015.
 */
public class QuetsalSchema {


    public static final String NAMESPACE = "http://quetsal.aksw.org/";

    public static final String PREFIX = "quetsal";

    public static Namespace NS = new SimpleNamespace(PREFIX, NAMESPACE);

    public static final IRI SUMMARIES;

    public static final IRI MODE ;

    public static final IRI COMMONPREDTHREASHOLD ;


    static  {
        ValueFactory vf = SimpleValueFactory.getInstance();
        SUMMARIES = vf.createIRI(NAMESPACE, "summariesFile");
        COMMONPREDTHREASHOLD = vf.createIRI(NAMESPACE, "commonPredThreshold");
        MODE = vf.createIRI(NAMESPACE, "hibiscusMode");
    }
}
