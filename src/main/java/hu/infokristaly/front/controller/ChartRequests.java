package hu.infokristaly.front.controller;

import hu.infokristaly.middle.service.ClientTypeService;
import hu.infokristaly.middle.service.ClientsService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.PieChartModel;
import java.io.Serializable;

@SessionScoped
@Named
public class ChartRequests implements Serializable {

    private static final long serialVersionUID = 8732063614865369805L;

    @Inject
    private Logger log;

    @Inject
    private ClientsService clientsService;

    @Inject
    private ClientTypeService clientTypeService;

    private PieChartModel pieModel;
    private BarChartModel barModel;
    private BarChartModel barModelByCategory;
    private LineChartModel historyLineModel;
    private Date year;
    private Integer iYear;

    @PostConstruct
    public void init() {
        pieModel = new PieChartModel();
        barModel = new BarChartModel();
        barModelByCategory = new BarChartModel();
        historyLineModel = new LineChartModel();
        initChartModels();
    }

    protected void initPie() {
        pieModel.setTitle("Regisztrált kliensek az ellátás jellege szerint");
        pieModel.setShowDataLabels(true);
        pieModel.setLegendPosition("w");
        if (iYear != null) {
            Calendar initYear = new GregorianCalendar(iYear, 11, 31);
            Map<String, Number> byType = clientsService.getCountByType(initYear.getTime());
            pieModel.setData(byType);
        } else {
            pieModel.getData().clear();
        }
    }

    protected void initLineAll() {
        if (year != null) {
            barModel.setLegendPosition("e");
            barModel.setTitle("Időszakban regisztrált kliensek");
            barModel.setShowPointLabels(true);
            final CategoryAxis xAxis = new CategoryAxis("Hónap");
            barModel.getAxes().put(AxisType.X, xAxis);
            Axis yAxis = barModel.getAxis(AxisType.Y);
            yAxis.setMin(0);
            Calendar initYear = new GregorianCalendar(iYear, 0, 1);
            ChartSeries seriesAllType = new ChartSeries();
            seriesAllType.setLabel("Kliensek");            
            Map<String, Number> monthly = clientsService.getCountByMonth(initYear.getTime(), null);
            monthly.forEach((k, v) -> {
                seriesAllType.set(k, v);
            });
            barModel.getSeries().clear();
            barModel.addSeries(seriesAllType);
        } else {
            barModel.getSeries().clear();
        }
    }

    protected void initByCategory() {
        if (year != null) {            
            barModelByCategory.setTitle("Időszakban regisztrált kliensek kategóriánként");
            barModelByCategory.setShowPointLabels(true);
            final CategoryAxis xAxis = new CategoryAxis("Hónap");
            barModelByCategory.getAxes().put(AxisType.X, xAxis);
            barModelByCategory.setBarWidth(20);            
            String legendPos = "e";
            barModelByCategory.setLegendPosition(legendPos);
            Axis yAxis = barModelByCategory.getAxis(AxisType.Y);
            yAxis.setMin(0);
            Calendar initYear = new GregorianCalendar(iYear, 0, 1);
            barModelByCategory.getSeries().clear();
            clientTypeService.findAll().stream().forEach(ct -> {
                ChartSeries seriesType = new ChartSeries();
                seriesType.setLabel(ct.getTypename());                
                Map<String, Number> monthlyByType = clientsService.getCountByMonth(initYear.getTime(), ct);                
                monthlyByType.forEach((k, v) -> {                    
                    seriesType.set(k, v);
                });
                barModelByCategory.addSeries(seriesType);
            });
        } else {
            barModelByCategory.getSeries().clear();
        }
    }

    protected void initHistory() {
        if (year != null) {
            historyLineModel.setLegendPosition("e");
            historyLineModel.setTitle("Foglalkozások");
            historyLineModel.setShowPointLabels(true);
            final CategoryAxis xAxis2 = new CategoryAxis("Hónap");
            historyLineModel.getAxes().put(AxisType.X, xAxis2);
            Axis yAxis2 = historyLineModel.getAxis(AxisType.Y);
            yAxis2.setMin(0);
            ChartSeries seriesAllType = new ChartSeries();
            seriesAllType.setLabel("Foglalkozások");

            Calendar initYear = new GregorianCalendar(iYear, 0, 1);
            Map<String, Number> history = clientsService.getEventCountByMonth(initYear.getTime(), null);
            history.forEach((k, v) -> {
                seriesAllType.set(k, v);
            });
            historyLineModel.getSeries().clear();
            historyLineModel.addSeries(seriesAllType);
        } else {
            historyLineModel.getSeries().clear();
        }
    }

    public void initChartModels() {
        initPie();
        initLineAll();
        initByCategory();
        initHistory();
    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public LineChartModel getHistoryLineModel() {
        return historyLineModel;
    }

    /**
     * @return the barModelByCategory
     */
    public BarChartModel getBarModelByCategory() {
        return barModelByCategory;
    }

    /**
     * @param barModelByCategory the barModelByCategory to set
     */
    public void setBarModelByCategory(BarChartModel barModelByCategory) {
        this.barModelByCategory = barModelByCategory;
    }

    /**
     * @return the year
     */
    public Date getYear() {
        return year;
    }

    /**
     * @param year
     *            the year to set
     */
    public void setYear(Date year) {
        this.year = year;
        if (year != null) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(year);
            this.iYear = cal.get(Calendar.YEAR);
        } else {
            this.iYear = null;
        }
        initChartModels();
    }

}
