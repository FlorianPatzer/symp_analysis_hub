package de.fraunhofer.iosb.svs.analysishub.views.masterdetail;

import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.PolicyDTOProjection;
import de.fraunhofer.iosb.svs.analysishub.data.service.PolicyService;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import java.util.Arrays;
import java.util.List;

/**
 * from org.vaadin.artur.helpers.CrudServiceDataProvider
 */
public class PolicyDataProvider<F> extends FilterablePageableDataProvider<PolicyDTOProjection, F> {
    private final PolicyService service;
    private List<QuerySortOrder> defaultSortOrders;

    public PolicyDataProvider(PolicyService service, QuerySortOrder... defaultSortOrders) {
        this.service = service;
        this.defaultSortOrders = Arrays.asList(defaultSortOrders);
    }

    protected Page<PolicyDTOProjection> fetchFromBackEnd(Query<PolicyDTOProjection, F> query, Pageable pageable) {
        return this.service.listDTOProjections(pageable);
    }

    protected List<QuerySortOrder> getDefaultSortOrders() {
        return this.defaultSortOrders;
    }

    protected int sizeInBackEnd(Query<PolicyDTOProjection, F> query) {
        return this.service.count();
    }
}
