package org.nasdanika.models.compare.ecore;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.ComparePackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.nasdanika.common.Context;
import org.nasdanika.common.NasdanikaException;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.Connection;
import org.nasdanika.drawio.Document;
import org.nasdanika.drawio.Layer;
import org.nasdanika.drawio.Model;
import org.nasdanika.drawio.Node;
import org.nasdanika.drawio.Page;
import org.nasdanika.drawio.Root;
import org.nasdanika.graph.emf.EObjectNode;
import org.nasdanika.graph.processor.NodeProcessorConfig;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;
import org.nasdanika.models.ecore.graph.processors.EClassNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;
import org.nasdanika.models.ecore.graph.processors.EDataTypeNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EEnumNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EPackageNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EPackageNodeProcessorFactory;

@EPackageNodeProcessorFactory(nsURI = ComparePackage.eNS_URI)
public class EcoreGenCompareProcessorsFactory {

	private Context context;
		
	public EcoreGenCompareProcessorsFactory(Context context) {
		this.context = context;
	}
	
	@EPackageNodeProcessorFactory(
			label = "EMF Compare Model",
			documentation =  """
				
				```drawio-resource
				compare.drawio
				```
					
				[Maven Central](https://central.sonatype.com/artifact/org.nasdanika.models.compare/model).	
					
				
				## Examples
				
				### Basic
				
				```java
				AttributeChange attrChange = CompareFactory.eINSTANCE.createAttributeChange();
				attrChange.setAttribute(ComparePackage.Literals.ATTRIBUTE_CHANGE__VALUE);
				
				AttributeChange oAttrChange = CompareFactory.eINSTANCE.createAttributeChange();
				oAttrChange.setAttribute(ComparePackage.Literals.COMPARISON__THREE_WAY);
				
				IComparisonScope scope = new DefaultComparisonScope(attrChange, oAttrChange, null);
				Comparison comparison = EMFCompare.builder().build().compare(scope);
				comparison.getDifferences().forEach(System.out::println);
				```
				
				### Comparing pom.xml versions
				
				Retrieves file revision with ``GitURIHandler`` using a tag, saves versions and the difference model to an XMI resource.
				
				```java
				org.nasdanika.models.maven.Model model = MavenFactory.eINSTANCE.createModel();
				File pomFile = new File("..\\..\\excel\\pom.xml").getCanonicalFile();
				try (InputStream in = new FileInputStream(pomFile)) {
					model.load(in);
				}
				System.out.println(model.getName());
				System.out.println(model.getGroupId());
				System.out.println(model.getArtifactId());
				System.out.println(model.getVersion());
				
				GitURIHandler gitURIHander = new GitURIHandler(pomFile);
				
				URI pomURI = URI.createURI("git://maven-2025.5.0/pom.xml");
				org.nasdanika.models.maven.Model gitModel = MavenFactory.eINSTANCE.createModel();
				try (InputStream in = gitURIHander.createInputStream(pomURI, null)) {
					gitModel.load(in);
				}
				System.out.println(gitModel.getName());
				System.out.println(gitModel.getGroupId());
				System.out.println(gitModel.getArtifactId());
				System.out.println(gitModel.getVersion());
				
				IComparisonScope scope = new DefaultComparisonScope(model, gitModel, null);
				Comparison comparison = EMFCompare.builder().build().compare(scope);
				comparison.getDifferences().forEach(System.out::println);	
				
				CapabilityLoader capabilityLoader = new CapabilityLoader();
				ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
				Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
				ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
				
				Resource resource = resourceSet.createResource(URI.createFileURI("target/compare.xml"));
				resource.getContents().add(comparison);
				resource.getContents().add(model);
				resource.getContents().add(gitModel);
				resource.save(null);						
				```
				
				[Output](https://github.com/Nasdanika-Models/compare/blob/main/model/test-output/pom-compare.xml)
				
				### Comparing Draw.io diagram versions
				
				Diagrams are loaded from input streams - one from classloader resource and the other from a Git commit. 
				Then the diagram documents are converted to the [diagram model](https://drawio.models.nasdanika.org/diagram.html).
				After that the models are compared and stored with the comparison result in a resource.
				
				```java
				GitURIHandler gitURIHander = new GitURIHandler();
				URI diagramURI = URI.createURI("git://5bfe5731bbdf10b742a3db53ca5e4dad0844732b/model/src/test/resources/org/nasdanika/models/compare/tests/test.drawio");
				URL resourceURL = getClass().getResource("test.drawio");
				try (InputStream in = resourceURL.openStream(); InputStream gitIn = gitURIHander.createInputStream(diagramURI, null)) {
					Document gitDocument = Document.load(in, diagramURI);
					Document document = Document.load(gitIn, URI.createURI(resourceURL.toString()));
				
					org.nasdanika.drawio.model.Document modelDocument = document.toModelDocument();
					org.nasdanika.drawio.model.Document gitModelDocument = gitDocument.toModelDocument();
					IComparisonScope scope = new DefaultComparisonScope(modelDocument, gitModelDocument, null);
					Comparison comparison = EMFCompare.builder().build().compare(scope);
					comparison.getDifferences().forEach(System.out::println);	
					
					CapabilityLoader capabilityLoader = new CapabilityLoader();
					ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
					Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
					ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
					
					Resource resource = resourceSet.createResource(URI.createFileURI("target/diagram-compare.xml"));
					resource.getContents().add(comparison);
					resource.getContents().add(modelDocument);
					resource.getContents().add(gitModelDocument);
					resource.save(null);
				}		
				```
				
				[Output](https://github.com/Nasdanika-Models/compare/blob/main/model/test-output/diagram-compare.xml)				
				
				"""
	)
	public EPackageNodeProcessor createEPackageProcessor(
			NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
			java.util.function.Function<ProgressMonitor, Action> prototypeProvider,
			BiConsumer<Label, ProgressMonitor> labelConfigurator,
			ProgressMonitor progressMonitor) {		
		return new EPackageNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}
			
			/**
			 * Generating a Drawio diagram
			 */
			@Override
			protected void generateDiagramAndGraphActions(Collection<Label> labels, ProgressMonitor progressMonitor) {
				super.generateDiagramAndGraphActions(labels, progressMonitor);
				
				try {
					Document document = Document.create(false, null);
					Page page = document.createPage();
					page.setName("Compare");
					
					Model model = page.getModel();
					Root root = model.getRoot();
					Layer backgroundLayer = root.getLayers().get(0);
					
					generateDrawioDiagram(
						ep -> backgroundLayer,	
						false, 
						false, 
						progressMonitor);
					
					org.nasdanika.drawio.Util.forceLayout(root, 1920, 1080);
												
					Files.writeString(new File("target/compare.drawio").toPath(), document.save(null));
				} catch (Exception e) {
					e.printStackTrace();
					throw new NasdanikaException(e);
				}								
			}			
			
		};
	}	
	
	@EClassifierNodeProcessorFactory
	public EClassifierNodeProcessor<?> createEClassifierProcessor(
			NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
			java.util.function.Function<ProgressMonitor, Action> prototypeProvider,
			BiConsumer<Label, ProgressMonitor> labelConfigurator,
			ProgressMonitor progressMonitor) {
		
		EObject eClassifier = ((EObjectNode) config.getElement()).get();
				
		if (eClassifier instanceof EClass) {
			return new EClassNodeProcessor(config, context, prototypeProvider) {
				
				@Override
				public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
					super.configureLabel(source, label, progressMonitor);
					if (labelConfigurator != null) {
						labelConfigurator.accept(label, progressMonitor);
					}
				}	
				
				@Override
				protected EList<? super Action> getMembersActionCollection(Action parent) {
					return parent.getChildren();
				}
				
				@Override
				protected EList<? super Action> getMembersCollection(Action membersAction) {
					return membersAction.getChildren();
				}
				
				@Override
				protected void addDiagramAction(Action action, Action diagramAction) {
					action.getSections().add(diagramAction);
				}
				
				@Override
				protected int getDiagramNodeWidth() {
					return Math.max(getTarget().getName().length() * 5, super.getDiagramNodeWidth());
				}
				
				@Override
				protected void createDrawioConnection(
						URI base, 
						Layer layer, 
						EClassifierNodeProcessor<?> dependency,
						Node diagramNode, Node targetNode) {
					
					EClassifier targetEClassifier = dependency.getTarget();
					
					// Supertype
					if (getTarget().getESuperTypes().contains(targetEClassifier)) {
						// TODO - connect top center of the sub-class to the bottom center of super-class
						Connection inheritance = layer.createConnection(diagramNode, targetNode);
						Map<String, String> style = inheritance.getStyle();
						style.put("edgeStyle", "orthogonalEdgeStyle");
						style.put("rounded", "0");
						style.put("orthogonalLoop", "1");
						style.put("jettySize", "auto");
						style.put("html", "1");
						style.put("endArrow", "block");
						style.put("endFill", "0");
					}
					
					// Reference
					for (EReference ref: getTarget().getEReferences()) {
						if (ref.getEType() == targetEClassifier) {
							Connection refConnection = layer.createConnection(diagramNode, targetNode);
							refConnection.setLabel(ref.getName());
							Map<String, String> style = refConnection.getStyle();
							style.put("rounded", "0");
							style.put("orthogonalLoop", "1");
							style.put("jettySize", "1");
							style.put("html", "1");
							if (ref.isMany()) {
								style.put("startArrow", "diamondThin");
								style.put("startFill", "1");
							}
							WidgetFactory refWidgetFactory = eReferenceWidgetFactories.get(ref.getName());
							if (refWidgetFactory != null) {
								Object refLink = refWidgetFactory.createLink(base, progressMonitor);
								if (refLink instanceof org.nasdanika.models.app.Link) {
									refConnection.setLink(((org.nasdanika.models.app.Link) refLink).getLocation());
								}								
							}
						}
					}
				}
				
			};
		}
		
		if (eClassifier instanceof EEnum) {
			return new EEnumNodeProcessor(config, context, prototypeProvider);
		}
		
		return new EDataTypeNodeProcessor<EDataType>(config, context, prototypeProvider);		
	}	
	

}
