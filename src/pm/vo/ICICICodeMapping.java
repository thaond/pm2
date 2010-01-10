package pm.vo;

public class ICICICodeMapping {
    private String iciciCode;
    private StockVO stock;

    public ICICICodeMapping() {
    }

    public ICICICodeMapping(String iciciCode, StockVO stockCode) {
        this.iciciCode = iciciCode;
        this.stock = stockCode;
    }

    public String getIciciCode() {
        return iciciCode;
    }

    public void setIciciCode(String iciciCode) {
        this.iciciCode = iciciCode;
    }

    public StockVO getStock() {
        return stock;
    }

    public void setStock(StockVO stock) {
        this.stock = stock;
    }

    public String getStockCode() {
        return stock.getStockCode();
    }

    public int getStockId() {
        return stock.getId();
    }
}

