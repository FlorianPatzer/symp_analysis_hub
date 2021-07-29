package de.fraunhofer.iosb.svs.analysishub.views.masterdetail;

import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.PolicyDTOProjection;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.PolicyImplementationDTOProjection;
import de.fraunhofer.iosb.svs.analysishub.data.service.PolicyImplementationService;
import de.fraunhofer.iosb.svs.analysishub.data.service.PolicyService;
import de.fraunhofer.iosb.svs.analysishub.views.main.MainView;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "master-detail", layout = MainView.class)
@PageTitle("Policies")
@CssImport("./styles/views/masterdetail/master-detail-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class MasterDetailView extends Div {

    private Grid<PolicyDTOProjection> grid = new Grid<>(PolicyDTOProjection.class, false);


    public MasterDetailView(@Autowired PolicyImplementationService policyImplementationService, @Autowired PolicyService policyService) {
        addClassName("master-detail-view");
        // Create UI
        setSizeFull();


        // Configure Grid
        grid.addColumn(PolicyDTOProjection::getLocalName).setAutoWidth(true).setHeader("Name");
        grid.addColumn(PolicyDTOProjection::getDescription).setAutoWidth(true).setHeader("Description");
        //grid.addColumn(policyDTOProjection -> policyService.getPolicyLink(policyDTOProjection.getLocalName())).setAutoWidth(true).setHeader("Link");
        grid.setDataProvider(new PolicyDataProvider<>(policyService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        //grid.setSelectionMode(Grid.SelectionMode.NONE);

        grid.setItemDetailsRenderer(new ComponentRenderer<>(policyDTOProjection ->
        {
            Grid<PolicyImplementationDTOProjection> nestedGrid = new Grid<>(PolicyImplementationDTOProjection.class, false);
            nestedGrid.addColumn(PolicyImplementationDTOProjection::getLocalName).setAutoWidth(true).setHeader("Name");
            nestedGrid.addColumn(PolicyImplementationDTOProjection::getDescription).setWidth("200px").setHeader("Description");
            nestedGrid.addColumn(policyImplementationDTOProjection -> policyImplementationService.getPolicyImplementationLink(policyImplementationDTOProjection.getLocalName())).setAutoWidth(true).setHeader("Link");
            nestedGrid.setDataProvider(new PolicyImplementationDataProvider<>(policyImplementationService, policyDTOProjection));
            return nestedGrid;
        }));

        add(grid);
    }
}
