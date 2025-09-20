package org.nasdanika.models.compare.tests;

import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.CompareFactory;
import org.eclipse.emf.compare.ComparePackage;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.junit.jupiter.api.Test;

public class CompareTests {

	@Test
	public void testCompare() throws Exception {
		AttributeChange attrChange = CompareFactory.eINSTANCE.createAttributeChange();
		attrChange.setAttribute(ComparePackage.Literals.ATTRIBUTE_CHANGE__VALUE);
		
		AttributeChange oAttrChange = CompareFactory.eINSTANCE.createAttributeChange();
		oAttrChange.setAttribute(ComparePackage.Literals.COMPARISON__THREE_WAY);
		
		IComparisonScope scope = new DefaultComparisonScope(attrChange, oAttrChange, null);
		Comparison comparison = EMFCompare.builder().build().compare(scope);
		comparison.getDifferences().forEach(System.out::println);		
	}
		
}
