package de.fraunhofer.iosb.svs.analysishub.views;

import de.fraunhofer.iosb.svs.analysishub.data.entity.Resource;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.ResourceUriProjection;
import de.fraunhofer.iosb.svs.analysishub.data.service.ResourceService;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import java.util.Arrays;
import java.util.List;

public class ResourceDataProvider<T extends Resource, F> extends FilterablePageableDataProvider<ResourceUriProjection, F> {

    private final ResourceService<T> service;
    private List<QuerySortOrder> defaultSortOrders;

    public ResourceDataProvider(ResourceService<T> service, QuerySortOrder... defaultSortOrders) {
        this.service = service;
        this.defaultSortOrders = Arrays.asList(defaultSortOrders);
    }

    @Override
    protected Page<ResourceUriProjection> fetchFromBackEnd(Query<ResourceUriProjection, F> query, Pageable pageable) {
        return this.service.listProjections(pageable);
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return this.defaultSortOrders;
    }

    @Override
    protected int sizeInBackEnd(Query<ResourceUriProjection, F> query) {
        return service.count();
    }
}
