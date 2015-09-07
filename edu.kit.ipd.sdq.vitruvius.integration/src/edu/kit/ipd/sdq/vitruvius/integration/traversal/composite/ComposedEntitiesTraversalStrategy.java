package edu.kit.ipd.sdq.vitruvius.integration.traversal.composite;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.core.composition.Connector;
import de.uka.ipd.sdq.pcm.core.entity.ComposedProvidingRequiringEntity;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.Role;
import de.uka.ipd.sdq.pcm.system.System;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.Change;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.CompositeChange;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.EMFModelChange;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.VURI;
import edu.kit.ipd.sdq.vitruvius.framework.meta.change.EChange;
import edu.kit.ipd.sdq.vitruvius.framework.meta.change.feature.list.InsertInEList;
import edu.kit.ipd.sdq.vitruvius.integration.traversal.EMFTraversalStrategy;
import edu.kit.ipd.sdq.vitruvius.integration.traversal.util.PCMChangeBuildHelper;

/**
 * This base class provides common methods used for traversing composed PCM entities that derive
 * from ComposedProvidingRequiringEntity
 *
 * It respects the ProvidedRolesMustBeBound-constraint and makes the same assumptions for
 * RequiredRoles.
 *
 * This demands a post-ordered depth-first traversal strategy.
 *
 * @author Sven Leonhardt
 *
 */
public abstract class ComposedEntitiesTraversalStrategy extends EMFTraversalStrategy {

    protected EList<Change> changeList;
    private final EList<ComposedProvidingRequiringEntity> traversedEntities = new BasicEList<ComposedProvidingRequiringEntity>();
    protected VURI vuri;

    /**
     * Recursive method implementing a post-order depth-first search.
     *
     * @param entity
     *            : the node to be traversed
     */
    protected void traverseComposedEntity(final ComposedProvidingRequiringEntity entity) {

        // found a confluent node, which has already been traversed? => nothing to do
        if (this.traversedEntities.contains(entity)) {
            return;
        }

        // Child nodes available?
        for (final AssemblyContext context : entity.getAssemblyContexts__ComposedStructure()) {

            // recursion
            final RepositoryComponent nextEntity = context.getEncapsulatedComponent__AssemblyContext();
            // BasicComponents are already traversed
            if (!(nextEntity instanceof BasicComponent) && !(entity instanceof System)) {
                this.traverseComposedEntity((ComposedProvidingRequiringEntity) nextEntity);
            }
        }

        // traverse context
        this.traverseAssemblyContexts(entity);

        // traverse roles and delegations
        this.traverseRolesAndDelegations(entity);

        // traverse assembly connectors
        this.traverseAssemblyConnectors(entity);

        // remember the already traversed entities
        this.traversedEntities.add(entity);

    }

    /**
     * traverses the assembly contexts.
     *
     * @param entity
     *            containing the contexts
     */
    private void traverseAssemblyContexts(final ComposedProvidingRequiringEntity entity) {

        for (final AssemblyContext context : entity.getAssemblyContexts__ComposedStructure()) {
            final EChange assemblyContextChange = PCMChangeBuildHelper.createChangeFromAssemblyContext(context);
            this.addChange(new EMFModelChange(assemblyContextChange, this.vuri), this.changeList);
        }

    }

    /**
     * traverses roles and delegations with respect to the AllRolesMustBeBound constraint for
     * composed entities.
     *
     * @param entity
     *            containing the roles and delegations
     */
    private void traverseRolesAndDelegations(final ComposedProvidingRequiringEntity entity) {

        // roleDelegationChanges should look like this: ((Role)(Delegation)+)*
        final EList<EChange> roleDelegationChanges = PCMChangeBuildHelper.createChangesFromRolesAndDelegations(entity);

        // last element a role?
        if (roleDelegationChanges.size() > 0) {
            @SuppressWarnings("unchecked")
            final InsertInEList<EObject> lastChange = (InsertInEList<EObject>) roleDelegationChanges
                    .get(roleDelegationChanges.size() - 1);
            if (lastChange.getNewValue() instanceof Role) {
                throw new IllegalArgumentException("The role " + ((Role) lastChange.getNewValue()).getEntityName()
                        + " does not have a delegation connector");
            }
        }

        CompositeChange compChange = null;

        for (int i = 0; i < roleDelegationChanges.size(); i++) {

            // set the role and the first delegation connector as composite change
            @SuppressWarnings("unchecked")
            InsertInEList<EObject> change = (InsertInEList<EObject>) roleDelegationChanges.get(i);
            if (change.getNewValue() instanceof Role) {
                compChange = new CompositeChange();
                compChange.addChange(new EMFModelChange(roleDelegationChanges.get(i), this.vuri));
                compChange.addChange(new EMFModelChange(roleDelegationChanges.get(i + 1), this.vuri));
                this.addChange(compChange, this.changeList);
                i++;
            } else {
                this.addChange(new EMFModelChange(roleDelegationChanges.get(i), this.vuri), this.changeList);
            }

        }

    }

    /**
     * traverses assembly connectors.
     *
     * @param entity
     *            containing the connectors
     */
    private void traverseAssemblyConnectors(final ComposedProvidingRequiringEntity entity) {

        for (final Connector connector : entity.getConnectors__ComposedStructure()) {

            if (connector instanceof AssemblyConnector) {
                final EChange assemblyConnectorChange = PCMChangeBuildHelper.createChangeFromConnector(connector);
                this.addChange(new EMFModelChange(assemblyConnectorChange, this.vuri), this.changeList);
            }

        }

    }

}
