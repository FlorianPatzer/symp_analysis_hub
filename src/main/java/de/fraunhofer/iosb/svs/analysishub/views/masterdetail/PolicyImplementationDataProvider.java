package de.fraunhofer.iosb.svs.analysishub.views.masterdetail;

import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.PolicyDTOProjection;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.PolicyImplementationDTOProjection;
import de.fraunhofer.iosb.svs.analysishub.data.service.PolicyImplementationService;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import java.util.Arrays;
import java.util.List;

public class PolicyImplementationDataProvider<F> extends FilterablePageableDataProvider<PolicyImplementationDTOProjection, F> {
    private final PolicyImplementationService service;
    private final PolicyDTOProjection policyDTOProjection;
    private List<QuerySortOrder> defaultSortOrders;

    public PolicyImplementationDataProvider(PolicyImplementationService service, PolicyDTOProjection policyDTOProjection, QuerySortOrder... defaultSortOrders) {
        this.service = service;
        this.policyDTOProjection = policyDTOProjection;
        this.defaultSortOrders = Arrays.asList(defaultSortOrders);
    }

    protected Page<PolicyImplementationDTOProjection> fetchFromBackEnd(Query<PolicyImplementationDTOProjection, F> query, Pageable pageable) {
        return this.service.listDTOProjectionsByPolicy(pageable, policyDTOProjection);
    }

    protected List<QuerySortOrder> getDefaultSortOrders() {
        return this.defaultSortOrders;
    }

    protected int sizeInBackEnd(Query<PolicyImplementationDTOProjection, F> query) {
        return this.service.countByPolicy(policyDTOProjection);
    }
}
