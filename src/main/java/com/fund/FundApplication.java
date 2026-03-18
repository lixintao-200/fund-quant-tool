package com.fund;

/**
 * 每天 14:50（下午 2 点 50 分） 运行代码
 * 下午3点前按结果操作
 * 这是基金交易的黄金时间
 *
 * @ClassName FundApplication
 * @Description TODO
 * @Author xintao.li
 * @Date 2026-03-18 21:23
 * @Version 1.0
 */

import org.jsoup.Jsoup;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.util.*;

@SpringBootApplication
public class FundApplication implements CommandLineRunner {

    @Resource
    private FundCrawlerService crawler;

    @Resource
    private FundQuantService quant;

    @Resource
    private ExcelReportService excel;

    public static void main(String[] args) {
        SpringApplication.run(FundApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("===== 西蒙斯基金量化工具 =====");
        System.out.println("功能1：监控我的基金");
        System.out.println("功能2：全市场扫描 TOP50 优质基金\n");

        List<String> fundCodes = Arrays.asList(
                "519771",
                "019875",
                "001122",
                "020713",
                "020640",
                "017470",
                "009982",
                "004243",
                "019458",
                "008706",
                "005064",
                "002162",
                "016453",
                "016702",
                "021634",
                "007789",
                "166301",
                "000386",
                "017668",
                "021075",
                "017992",
                "017825",
                "020406",
                "004643",
                "004433",
                "017141",
                "022653",
                "022347",
                "022084",
                "021662",
                "019305",
                "012860",
                "015693"
        );

        List<FundResult> resultList = new ArrayList<>();

        for (String code : fundCodes) {
            System.out.println("正在爬取：" + code);
            List<FundData> data = crawler.crawl(code);

            if (data.size() >= 5) {  // 数据足够才分析
                FundResult result = quant.analyze(data);
                resultList.add(result);
                System.out.println("【" + result.getName() + "】 信号：" + result.getSignal());
            } else {
                System.out.println("【" + code + "】数据不足，跳过");
            }

            crawler.sleep(); // 间隔2秒，超级安全
        }

        excel.create(resultList, "D:/我的基金今日决策.xlsx");

        // ======================
        // 功能2：全市场扫描 TOP50  每周日执行一次  绝对安全、完全合法
        // ======================
        // System.out.println("\n===== 全市场基金扫描中 =====");
        // Set<String> allCodes = new HashSet<>();
        // String[] types = {"gp","hh","zs","qdii","fof"};
        //
        // for (String t : types) {
        //     try {
        //         String url = "https://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft="+t+"&pi=1&pn=500";
        //         String txt = Jsoup.connect(url).userAgent("Mozilla/5.0").ignoreContentType(true).timeout(10000).get().body().text();
        //         String[] arr = txt.split(",");
        //         for (String s : arr) {
        //             if (s.matches("\\d{6}")) allCodes.add(s);
        //         }
        //     } catch (Exception ignored) {}
        // }
        //
        // List<FundResult> allResults = new ArrayList<>();
        // for (String code : allCodes) {
        //     try {
        //         List<FundData> d = crawler.crawl(code);
        //         if (d.size()>=20) allResults.add(quant.analyze(d));
        //     } catch (Exception ignored) {}
        //     crawler.sleep();
        // }
        //
        // List<FundResult> top50 = quant.getTop50(allResults);
        // System.out.println("\n===== 全市场最优基金 TOP50 =====");
        // top50.forEach(r-> System.out.println(r.getScore()+" | "+r.getName()+" | "+r.getRankLevel()));
        //
        // excel.create(top50, "D:/全市场最优基金TOP50.xlsx");
        // System.out.println("\n✅ 全部完成！");


        // ======================
// 功能3：安全版 → 从优质基金池选 TOP50（只爬500，安全快速）  每周日执行一次  绝对安全、完全合法
// ======================
//         System.out.println("\n===== 全市场优质基金扫描（安全版） =====");
//         Set<String> allCodes = new HashSet<>();
//
//         try {
//             // 只爬【天天基金排行 - 全部基金】前 500 只（优质基金池）
//             String url = "https://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=all&pi=1&pn=500";
//             String txt = Jsoup.connect(url)
//                     .userAgent("Mozilla/5.0")
//                     .ignoreContentType(true)
//                     .timeout(10000)
//                     .get().body().text();
//
//             String[] arr = txt.split(",");
//             for (String s : arr) {
//                 if (s.matches("\\d{6}")) {
//                     allCodes.add(s);
//                 }
//             }
//         } catch (Exception ignored) {
//         }
//
//         List<FundResult> allResults = new ArrayList<>();
//         for (String code : allCodes) {
//             try {
//                 List<FundData> d = crawler.crawl(code);
//                 if (d.size() >= 20) {
//                     allResults.add(quant.analyze(d));
//                 }
//             } catch (Exception ignored) {
//             }
//             crawler.sleep(); // 2秒间隔
//         }
//
// // 排序取 TOP50
//         List<FundResult> top50 = quant.getTop50(allResults);
//         System.out.println("\n===== 全市场最优基金 TOP50 =====");
//         top50.forEach(r -> System.out.println(r.getScore() + " | " + r.getName() + " | " + r.getRankLevel()));
//
//         excel.create(top50, "D:/全市场最优基金TOP50.xlsx");
//         System.out.println("\n✅ 全部完成！");

// ======================
// 功能4：极速版 → 1分钟获取【全市场最强TOP5】（14:50专用）
// ======================

        System.out.println("\n===== 极速扫描 → 全市场最强TOP5基金 =====");
        Set<String> allCodes = new HashSet<>();

        try {
            // 只爬【天天基金官方排行榜】前100只优质基金（最强基金都在这里）
            String url = "https://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=all&pi=1&pn=100";
            String txt = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                    .ignoreContentType(true)
                    .timeout(10000)
                    .get().body().text();

            String[] arr = txt.split(",");
            for (String s : arr) {
                if (s.matches("\\d{6}")) {
                    allCodes.add(s);
                }
            }
        } catch (Exception ignored) {
        }

        System.out.println("本次扫描基金数量：" + allCodes.size());

// 只扫描100只 → 极速出结果
        List<FundResult> allResults = new ArrayList<>();
        for (String code : allCodes) {
            try {
                List<FundData> d = crawler.crawl(code);
                if (d.size() >= 20) {
                    FundResult result = quant.analyze(d);
                    if (result != null) {  // 这里修复：确保 result 不为空
                        allResults.add(result);
                    }
                }
            } catch (Exception ignored) {
            }
            crawler.sleep(); // 2秒温柔间隔
        }

// 修复：如果没有数据，给提示
        if (allResults.isEmpty()) {
            System.out.println("⚠️ 暂无符合条件的基金数据");
            return;
        }

// 直接取 TOP5（你要的最终结果）
        List<FundResult> top5 = allResults.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(5)
                .toList();

        System.out.println("\n========================================");
        System.out.println("        🔥 全市场最强基金 TOP5 🔥");
        System.out.println("========================================");
        for (int i = 0; i < top5.size(); i++) {
            FundResult r = top5.get(i);
            System.out.println((i + 1) + ". " + r.getName() + " | 得分：" + r.getScore() + " | " + r.getRankLevel());
        }

// 导出Excel
        try {
            excel.create(top5, "D:/每日最强TOP5基金.xlsx");
            System.out.println("\n✅ Excel 已生成：D:/每日最强TOP5基金.xlsx");
        } catch (Exception e) {
            System.out.println("❌ Excel 生成失败：" + e.getMessage());
        }


    }
}
