package hu.infokristaly.front.view;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.MapModel;

@Named
@RequestScoped
public class ChartView implements Serializable {
    
    private static final long serialVersionUID = -3294097081504669489L;

    private LineChartModel dateModel;
    private LineChartSeries series1 = new LineChartSeries();
    private MapModel emptyModel;
    
    @PostConstruct
    public void init() {
        createDateModel();
        emptyModel = new DefaultMapModel();
    }
 
    public LineChartModel getDateModel() {
        return dateModel;
    }
     
    private void createDateModel() {
        dateModel = new LineChartModel();
        series1.setLabel("Memory usage");
        
        dateModel.setTitle("Zoom for Details");
        dateModel.setZoom(true);
        dateModel.getAxis(AxisType.Y).setLabel("Values");
        DateAxis axis = new DateAxis("Dates");
        axis.setTickAngle(-50);
        axis.setMax("2014-02-01");
        axis.setTickFormat("%b %#d, %y");
        dateModel.getAxes().put(AxisType.X, axis);

        series1.set("2014-01-01",0);
        Runtime rt = Runtime.getRuntime();
        series1.set("2014-02-01",rt.totalMemory() - rt.freeMemory()  / 1024F);
        series1.set("2014-02-02",rt.totalMemory() - rt.freeMemory()  / 1024F);
        dateModel.addSeries(series1);
}
    
    public void updateModel() {
        Runtime rt = Runtime.getRuntime();
        series1.set("2014-02-03",rt.totalMemory() - rt.freeMemory()  / 1024F);
    }

    /**
     * @return the emptyModel
     */
    public MapModel getEmptyModel() {
        return emptyModel;
    }

    /**
     * @param emptyModel the emptyModel to set
     */
    public void setEmptyModel(MapModel emptyModel) {
        this.emptyModel = emptyModel;
    }
    
}
