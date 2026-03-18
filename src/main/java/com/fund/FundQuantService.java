package com.fund;

/**
 * @ClassName FundQuantService
 * @Description TODO
 * @Author xintao.li
 * @Date 2026-03-18 21:22
 * @Version 1.0
 */

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FundQuantService {


    /**
     * 西蒙斯 4 大严格买入条件（必须全部满足）
     * 净值站在 20 日均线上方（趋势向上）
     * 5 日动量为正（短期向上）
     * 20 日动量为正（中期向上）
     * 不暴涨、不暴跌（健康上涨）
     * @param dataList
     * @return
     */
    public FundResult analyze(List<FundData> dataList) {
        if (dataList.size() < 20) return null;

        // 按时间倒序（最新在前）
        dataList.sort(Comparator.comparing(FundData::getDate).reversed());
        FundData latest = dataList.get(0);
        double net = latest.getNetValue();

        // 计算均线 & 涨幅
        double ma20 = calcMa(dataList, 20);
        double net5 = dataList.get(4).getNetValue();
        double rise5d = (net - net5) / net5;
        double net20 = dataList.get(19).getNetValue();
        double rise20d = (net - net20) / net20;

        // ==============================================
        // 🔥 西蒙斯 4 大严格买入条件（必须全部满足）
        // ==============================================
        boolean cond1 = net > ma20;                    // 1. 站在20日均线上方
        boolean cond2 = rise5d > 0;                    // 2. 短期动量向上 今天和5天相比涨了
        boolean cond3 = rise20d > 0;                   // 3. 中期动量向上 今天和20天相比涨了
        boolean cond4 = rise5d < 0.08;                 // 4. 不暴涨（健康上涨）

        String signal;
        // ✅ 4 个条件全部满足 → 买入
        if (cond1 && cond2 && cond3 && cond4) {
            signal = "买入";
        }
        // ❌ 双空头 → 卖出
        else if (!cond1 && !cond2) {
            signal = "卖出";
        }
        // ⏸️ 其他情况 → 观望
        else {
            signal = "观望";
        }

        String trend = cond1 ? "多头" : "空仓";
        String momentum = cond2 ? "强" : "弱";

        String suggest = switch (signal) {
            case "买入" -> "单只≤10%，总仓≤60%";
            case "卖出" -> "立即清仓";
            default -> "轻仓/持有不动";
        };

        // 西蒙斯评分系统
        double score = calculateScore(latest, ma20, rise5d, rise20d);
        String rankLevel = getRankLevel(score);

        FundResult res = new FundResult();
        res.setCode(latest.getCode());
        res.setName(latest.getName());
        res.setLatestNet(net);
        res.setMa20(ma20);
        res.setRise5d(rise5d);
        res.setRise20d(rise20d);
        res.setTrend(trend);
        res.setMomentum(momentum);
        res.setSignal(signal);
        res.setSuggest(suggest);
        res.setScore(score);
        res.setRankLevel(rankLevel);

        return res;
    }
    // public FundResult analyze(List<FundData> dataList) {
    //     if (dataList.size() < 5) return null;
    //
    //     dataList.sort(Comparator.comparing(FundData::getDate).reversed());
    //     FundData latest = dataList.get(0);
    //     double net = latest.getNetValue();
    //
    //     // 1. MA20(20日均线)
    //     // 取最近 20 个交易日的净值，求平均值
    //     // 每天自动更新一次
    //     // 代表最近一个月的平均成本位置
    //     // MA20 向上 → 大多数人在赚钱，趋势偏强
    //     // MA20 向下 → 大多数人在亏钱，趋势偏弱
    //     double ma20 = calcMa(dataList, 20);
    //
    //     // 2. 5日涨幅
    //     double net5 = dataList.get(Math.min(4, dataList.size() - 1)).getNetValue();
    //     double rise5d = (net - net5) / net5;
    //
    //     // 3. 20日涨幅
    //     double net20 = dataList.get(Math.min(19, dataList.size() - 1)).getNetValue();
    //     double rise20d = (net - net20) / net20;
    //
    //     // 4. 趋势
    //     //多头 : 最新净值 > MA20 → 价格在均线之上 → 上升趋势   , 现在涨势良好，处于上升通道，适合做多
    //     // 空仓:  最新净值 < MA20 → 价格在均线之下 → 下降趋势  ,现在走势偏弱，亏钱概率大，适合离场或不参与。
    //     String trend = net > ma20 ? "多头" : "空仓";
    //
    //     // 5. 动量
    //     String momentum = rise5d > 0 ? "强" : "弱";
    //
    //     // 6. 西蒙斯信号
    //     String signal;
    //     if ("多头".equals(trend) && "强".equals(momentum)) {
    //         signal = "买入";
    //     } else if ("空仓".equals(trend) && "弱".equals(momentum)) {
    //         signal = "卖出";
    //     } else {
    //         signal = "观望";  //不买也不卖
    //     }
    //
    //     String suggest = switch (signal) {
    //         case "买入" -> "单只≤10%，总仓≤60%";  // 单只≤10%：不要把钱押在一只基金上  总仓≤60%：永远不要满仓，手里必须留现金
    //         case "卖出" -> "立即清仓";
    //         default -> "轻仓/持有";
    //     };
    //
    //     double score = calculateScore(latest, ma20, rise5d, rise20d);
    //     String rankLevel = getRankLevel(score);
    //
    //     FundResult res = new FundResult();
    //     res.setCode(latest.getCode());
    //     res.setName(latest.getName());
    //     res.setLatestNet(net);
    //     res.setMa20(ma20);
    //     res.setRise5d(rise5d);
    //     res.setRise20d(rise20d);
    //     res.setTrend(trend);
    //     res.setMomentum(momentum);
    //     res.setSignal(signal);
    //     res.setSuggest(suggest);
    //     res.setScore(score);
    //     res.setRankLevel(rankLevel);
    //
    //     return res;
    // }

    private double calcMa(List<FundData> list, int n) {
        int len = Math.min(n, list.size());
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += list.get(i).getNetValue();
        }
        return sum / len;
    }

    // 按得分从高到低排序，取TOP50
    public List<FundResult> getTop50(List<FundResult> list) {
        list.sort(Comparator.comparingDouble(FundResult::getScore).reversed());
        return list.stream().limit(50).collect(Collectors.toList());
    }


    // 多维度量化评分（西蒙斯核心）
    // 🔥 正宗西蒙斯量化评分模型（低风险 + 稳健 + 趋势 + 动量）
    private double calculateScore(FundData latest, double ma20, double rise5d, double rise20d) {
        double score = 0.0;
        double net = latest.getNetValue();

        // ==============================
        // 1. 趋势强度 35%（西蒙斯核心）
        // ==============================
        boolean ma20Up = net > ma20;
        boolean ma20Strong = net > ma20 * 1.01; // 温和站在均线上方

        if (ma20Up) score += 25;
        if (ma20Strong) score += 10;

        // ==============================
        // 2. 稳健动量 25%（不追涨）
        // ==============================
        double momentum = (rise5d + rise20d / 2) * 100;
        momentum = Math.max(0, Math.min(25, momentum)); // 限制涨幅，不追高
        score += momentum;

        // ==============================
        // 3. 低风险评分 25%（西蒙斯最看重！）
        // ==============================
        boolean healthyTrend = rise20d > 0 && rise5d > -0.02; // 不大跌
        if (healthyTrend) score += 25;

        // ==============================
        // 4. 健康上涨区域 15%
        // ==============================
        double ratio = net / ma20;
        boolean healthy = ratio > 1.0 && ratio < 1.08; // 温和上涨，不暴涨
        if (healthy) score += 15;

        // 最终得分封顶 100
        return Math.min(100.0, Math.round(score * 100.0) / 100.0);
    }

    private double calculateScore(double net, double ma20, double rise5d, double rise20d) {
        double score = 0;
        if (net > ma20) score += 40;
        score += Math.min(30, rise5d * 100 * 3);
        if (rise20d > 0) score += 20;
        double ratio = net / ma20;
        if (ratio > 1.0 && ratio < 1.1) score +=10;
        return Math.min(100, Math.round(score*100)/100.0);
    }



    private String getRankLevel(double score) {
        if (score >= 80) return "强烈买入";
        if (score >= 60) return "优选买入";
        if (score >= 40) return "观望";
        return "卖出";
    }
}
