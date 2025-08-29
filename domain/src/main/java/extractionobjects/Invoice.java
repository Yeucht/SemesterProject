package extractionobjects;

import java.util.Date;

public class Invoice {
    private double bill = 0;
    private Date dateStart;
    private Date dateEnd;
    private double priceKwh;
    private double totalComsumption;
    private int serialNumber;

    public Invoice(Date dateStart, Date dateEnd, double priceKwh, double totalConsumption, int serialNumber) {

        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.priceKwh = priceKwh;
        this.bill = priceKwh * totalConsumption;
        this.totalComsumption = totalConsumption;
        this.serialNumber = serialNumber;
    }

    public Invoice(){}

    public double getBill() { return bill; }
    public Date getDateStart() { return dateStart; }
    public Date getDateEnd() { return dateEnd; }
    public double getPriceKwh() { return priceKwh; }
    public double getTotalConsumption() { return totalComsumption; }

    public void setBill(double bill) { this.bill = bill; }
    public void setDateStart(Date dateStart) { this.dateStart = dateStart; }
    public void setDateEnd(Date dateEnd) { this.dateEnd = dateEnd; }
    public void setPriceKwh(double priceKwh) { this.priceKwh = priceKwh; }
    public void setTotalConsumption(double totalComsumption) { this.totalComsumption = totalComsumption; }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }
    public int getSerialNumber() { return serialNumber; }
}
