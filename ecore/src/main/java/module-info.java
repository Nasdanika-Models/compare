import org.nasdanika.capability.CapabilityFactory;
import org.nasdanika.models.compare.ecore.ECoreGenCompareProcessorsCapabilityFactory;

module org.nasdanika.models.compare.ecore {
		
	requires transitive org.nasdanika.models.compare;
	requires transitive org.nasdanika.models.ecore.graph;
	
	exports org.nasdanika.models.compare.ecore;
	opens org.nasdanika.models.compare.ecore; // For loading resources

	provides CapabilityFactory with	ECoreGenCompareProcessorsCapabilityFactory; 		
	
}
