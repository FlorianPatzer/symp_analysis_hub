package de.fraunhofer.iosb.svs.analysishub.views.addpolicyanalysis;

import de.fraunhofer.iosb.svs.analysishub.data.entity.Query;
import de.fraunhofer.iosb.svs.analysishub.data.service.QueryService;

import com.vaadin.flow.data.provider.QuerySortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import java.util.Arrays;
import java.util.List;

public class QueryDataProvider<F> extends FilterablePageableDataProvider<Query, F> {

    private final QueryService service;
    private List<QuerySortOrder> defaultSortOrders;

    public QueryDataProvider(QueryService service, QuerySortOrder... defaultSortOrders) {
        this.service = service;
        this.defaultSortOrders = Arrays.asList(defaultSortOrders);
    }

    @Override
    protected Page<Query> fetchFromBackEnd(com.vaadin.flow.data.provider.Query<Query, F> query, Pageable pageable) {
        return this.service.findAll(pageable);
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return this.defaultSortOrders;
    }

    @Override
    protected int sizeInBackEnd(com.vaadin.flow.data.provider.Query<Query, F> query) {
        return service.count();
    }
}
