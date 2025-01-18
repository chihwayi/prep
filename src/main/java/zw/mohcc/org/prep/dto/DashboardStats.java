package zw.mohcc.org.prep.dto;


import java.util.Map;

public class DashboardStats {
    private int totalPatients;
    private int activePatients;
    private int visitsThisMonth;
    private Map<String, Integer> statusDistribution;
    private Map<String, Integer> populationTypeDistribution;

    public DashboardStats(int totalPatients, int activePatients, int visitsThisMonth,
                          Map<String, Integer> statusDistribution,
                          Map<String, Integer> populationTypeDistribution) {
        this.totalPatients = totalPatients;
        this.activePatients = activePatients;
        this.visitsThisMonth = visitsThisMonth;
        this.statusDistribution = statusDistribution;
        this.populationTypeDistribution = populationTypeDistribution;
    }

    public int getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(int totalPatients) {
        this.totalPatients = totalPatients;
    }

    public int getActivePatients() {
        return activePatients;
    }

    public void setActivePatients(int activePatients) {
        this.activePatients = activePatients;
    }

    public int getVisitsThisMonth() {
        return visitsThisMonth;
    }

    public void setVisitsThisMonth(int visitsThisMonth) {
        this.visitsThisMonth = visitsThisMonth;
    }

    public Map<String, Integer> getStatusDistribution() {
        return statusDistribution;
    }

    public void setStatusDistribution(Map<String, Integer> statusDistribution) {
        this.statusDistribution = statusDistribution;
    }

    public Map<String, Integer> getPopulationTypeDistribution() {
        return populationTypeDistribution;
    }

    public void setPopulationTypeDistribution(Map<String, Integer> populationTypeDistribution) {
        this.populationTypeDistribution = populationTypeDistribution;
    }
}
