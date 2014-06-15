package tutorial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.sap.research.usdl.module.service.ServicePackage;
import com.sap.research.usdl.module.usdl.USDL3Document;

public class HelloRDFWorld {
	public final static String USDL = "http://www.linked-usdl.org/ns/usdl#";
	public final static String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public final static String OWL = "http://www.w3.org/2002/07/owl#";
	public final static String DC = "http://purl.org/dc/elements/1.1/";
	public final static String XSD = "http://www.w3.org/2001/XMLSchema#";
	public final static String VANN = "http://purl.org/vocab/vann/";
	public final static String FOAF = "http://xmlns.com/foaf/0.1/";
	public final static String USDK = "http://www.linked-usdl.org/ns/usdl#";
	public final static String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	public final static String GR = "http://purl.org/goodrelations/v1#";
	public final static String SKOS = "http://www.w3.org/2004/02/skos/core#";
	public final static String ORG = "http://www.w3.org/ns/org#";
	public final static String PRICE = "http://www.linked-usdl.org/ns/usdl-price#";
	public final static String LEGAL = "http://www.linked-usdl.org/ns/usdl-legal#";
	public final static String DEI = "http://dei.uc.pt/rdf/dei#";

	// public final static String USDL_Core_Schema_File = "./usdl-core.ttl";
	// public final static String USDL_Price_Schema_File = "./usdl-price.ttl";

	static final String inputFileName = "./service";
	static final String outputFileName = "./src/main/java/service_final.ttl";

	public static void main(String args[]) throws IOException {
		HelloRDFWorld project = new HelloRDFWorld();

		ServicePackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("usdl", new XMIResourceFactoryImpl());

		ResourceSet resSet = new ResourceSetImpl();

		Resource resource = resSet.getResource(
				URI.createURI("src/main/java/service.usdl"), true);

		USDL3Document document = (USDL3Document) resource.getContents().get(0);

		// --------------------------------------------------------- //

		Model model = ModelFactory.createDefaultModel();

		model = project.populateUSDLmodel(model, document);

		// project.displayUSDLmodel(model, "TTL");

		project.writeUSDLmodeltoFile(model, outputFileName, "TTL");

		project.writeModel(model);

		/*
		 * OutputStream out = new FileOutputStream(outputFileName);
		 * out.write(prefix.getBytes()); out.close(); OutputStream add = new
		 * FileOutputStream(outputFileName, true); model.write(add, "TTL");
		 */

	}

	public Model populateUSDLmodel(Model model, USDL3Document document) {
		// nome do serviço
		String Name = document.getServices().get(0).getNames().get(0)
				.getValue();

		// crio o serviço com o nome recuperado
		com.hp.hpl.jena.rdf.model.Resource service = model.createResource(Name);

		for (int i = 0; i < 8; i++) {
			// nome da propriedade
			String nameProperty = (document.getServices().get(0)
					.getCapabilities().get(i).getNames().get(0).getValue())
					.replace(" ", "").replace("/", "");

			// crio uma propriedade
			Property p = model.createProperty(RDF + nameProperty);

			// descrição da propriedade
			String property = document.getServices().get(0).getCapabilities()
					.get(i).getDescriptions().get(0).getValue();

			// add a propriedade e sua descrição no serviço
			service.addProperty(p, property, XSDDatatype.XSDstring);
		}

		return model;
	}

	public void displayUSDLmodel(Model model, String lang) {
		model.write(System.out, lang, USDL);
	}

	public void writeUSDLmodeltoFile(Model m, String filePath, String lang) {

		m.setNsPrefix("usdl", USDL);
		m.setNsPrefix("rdf", RDF);
		m.setNsPrefix("owl", OWL);
		m.setNsPrefix("dc", DC);
		m.setNsPrefix("xsd", XSD);
		m.setNsPrefix("vann", VANN);
		m.setNsPrefix("foaf", FOAF);
		m.setNsPrefix("rdfs", RDFS);
		m.setNsPrefix("gr", GR);
		m.setNsPrefix("skos", SKOS);
		m.setNsPrefix("org", ORG);
		m.setNsPrefix("price", PRICE);
		m.setNsPrefix("legal", LEGAL);
		m.setNsPrefix("dei", DEI);

		try {
			File outputFile = new File(filePath);
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(outputFile);
			m.write(out, lang);
			out.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	public void writeModel(Model model) {
		// list the statements in the Model
		StmtIterator iter = model.listStatements();
		// com.hp.hpl.jena.rdf.model.RDFNode
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement(); // get next statement
			com.hp.hpl.jena.rdf.model.Resource subject = stmt.getSubject(); // get
																			// the
																			// subject
			// get the predicate
			com.hp.hpl.jena.rdf.model.Property predicate = stmt.getPredicate();
			RDFNode object = stmt.getObject(); // get the object

			System.out.print(subject.toString());
			System.out.print(" " + predicate.getLocalName().toString() + ": ");
			// System.out.print(" \"-------" + object.toString());
			System.out.println(StringUtils.substringBetween(object.toString(),
					"", "^"));
		}
	}
}
