package tools.vitruv.applications.cbs.commonalities;

import java.util.Set;

import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.pcm.PcmDomainProvider;
import tools.vitruv.domains.uml.UmlDomainProvider;
import tools.vitruv.framework.applications.VitruvApplication;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.commonalities.CommonalitiesChangePropagationSpecificationProvider;
import tools.vitruv.framework.propagation.ChangePropagationSpecification;

public class CbsCommonalitiesApplication implements VitruvApplication {
	@Override
	public Set<ChangePropagationSpecification> getChangePropagationSpecifications() {
		return CommonalitiesChangePropagationSpecificationProvider.getChangePropagationSpecifications();
	}

	@Override
	public String getName() {
		return "CBS Commonalities";
	}

	@Override
	public Set<VitruvDomain> getVitruvDomains() {
		return Set.of(new PcmDomainProvider().getDomain(), new UmlDomainProvider().getDomain(),
				new JavaDomainProvider().getDomain());
	}

}