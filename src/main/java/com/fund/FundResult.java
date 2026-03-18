package com.fund;

/**
 * @ClassName FundResult
 * @Description TODO
 * @Author xintao.li
 * @Date 2026-03-18 21:21
 * @Version 1.0
 */

public class FundResult {
    private String code;
    private String name;
    private double latestNet;
    private double ma20;
    private double rise5d;
    private double rise20d;
    private String trend;    // 多头/空仓
    private String momentum;// 强/弱
    private String signal;   // 买入/卖出/观望
    private String suggest;  // 仓位建议

    private double score;        // 量化总分（0-100）
    private String rankLevel;   // 评级：强烈买入/优选买入/观望/卖出

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatestNet() {
        return latestNet;
    }

    public void setLatestNet(double latestNet) {
        this.latestNet = latestNet;
    }

    public double getMa20() {
        return ma20;
    }

    public void setMa20(double ma20) {
        this.ma20 = ma20;
    }

    public double getRise5d() {
        return rise5d;
    }

    public void setRise5d(double rise5d) {
        this.rise5d = rise5d;
    }

    public double getRise20d() {
        return rise20d;
    }

    public void setRise20d(double rise20d) {
        this.rise20d = rise20d;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public String getMomentum() {
        return momentum;
    }

    public void setMomentum(String momentum) {
        this.momentum = momentum;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getRankLevel() {
        return rankLevel;
    }

    public void setRankLevel(String rankLevel) {
        this.rankLevel = rankLevel;
    }
}
