/*******************************************************************************
 * Copyright (c) 2012, 2014 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.compare.merge;

import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.ConflictKind;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceState;

/**
 * A simple merger for pseudo conflict. It only marks the differences as merged without doing anything except
 * browsing the requirements and the equivalences.
 * 
 * @author <a href="mailto:mikael.barbero@obeo.fr">Mikael Barbero</a>
 */
public class PseudoConflictMerger extends AbstractMerger {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.merge.IMerger#isMergerFor(org.eclipse.emf.compare.Diff)
	 */
	public boolean isMergerFor(Diff target) {
		return target.getConflict() != null && target.getConflict().getKind() == ConflictKind.PSEUDO;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.merge.AbstractMerger#copyLeftToRight(org.eclipse.emf.compare.Diff,
	 *      org.eclipse.emf.common.util.Monitor)
	 */
	@Override
	public void copyLeftToRight(Diff target, Monitor monitor) {
		super.copyLeftToRight(target, monitor);
		for (Diff pseudoConflictedDiff : target.getConflict().getDifferences()) {
			pseudoConflictedDiff.setState(DifferenceState.MERGED);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.merge.AbstractMerger#copyRightToLeft(org.eclipse.emf.compare.Diff,
	 *      org.eclipse.emf.common.util.Monitor)
	 */
	@Override
	public void copyRightToLeft(Diff target, Monitor monitor) {
		super.copyRightToLeft(target, monitor);
		for (Diff pseudoConflictedDiff : target.getConflict().getDifferences()) {
			pseudoConflictedDiff.setState(DifferenceState.MERGED);
		}
	}
}
