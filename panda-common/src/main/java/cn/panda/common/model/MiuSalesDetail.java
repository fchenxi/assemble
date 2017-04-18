package cn.panda.common.model;

import java.io.Serializable;

/**
 * @author      tong.cx
 * @version     0.1.0
 * @datetime    2016/3/28 18:42
 * @copyright   wonhigh.cn
 */
public class MiuSalesDetail implements Serializable{
    private String id;
    private String brand;
    private int price;
    private int salesTotal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSalesTotal() {
        return salesTotal;
    }

    public void setSalesTotal(int salesTotal) {
        this.salesTotal = salesTotal;
    }

    public MiuSalesDetail() {
    }

    public MiuSalesDetail(String id, String brand, int price, int salesTotal) {
        this.id = id;
        this.brand = brand;
        this.price = price;
        this.salesTotal = salesTotal;
    }

    @Override
    public String toString() {
        return "MiuSalesDetail{" +
                "id='" + id + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", salesTotal=" + salesTotal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MiuSalesDetail that = (MiuSalesDetail) o;

        if (price != that.price) return false;
        if (salesTotal != that.salesTotal) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return brand != null ? brand.equals(that.brand) : that.brand == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (brand != null ? brand.hashCode() : 0);
        result = 31 * result + price;
        result = 31 * result + (int) (salesTotal ^ (salesTotal >>> 32));
        return result;
    }
}
