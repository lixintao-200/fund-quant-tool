package com.fund;

/**
 * @ClassName FundData
 * @Description TODO
 * @Author xintao.li
 * @Date 2026-03-18 21:20
 * @Version 1.0
 */
import java.time.LocalDate;

import java.time.LocalDate;

public class FundData {
    private String code;
    private String name;
    private LocalDate date;
    private double netValue;

    public String getCode() {return code;}
    public void setCode(String code) {this.code = code;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}
    public double getNetValue() {return netValue;}
    public void setNetValue(double netValue) {this.netValue = netValue;}
}
