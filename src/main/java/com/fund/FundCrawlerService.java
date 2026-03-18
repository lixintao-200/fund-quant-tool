package com.fund;

/**
 * @ClassName FundCrawlerService
 * @Description TODO
 * @Author xintao.li
 * @Date 2026-03-18 21:21
 * @Version 1.0
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FundCrawlerService {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
    private static final int CRAWL_INTERVAL_SECONDS = 2;

    // 爬取历史净值
    public List<FundData> crawl(String code) {
        List<FundData> list = new ArrayList<>();
        String url = "https://fund.eastmoney.com/f10/F10DataApi.aspx?type=lsjz&code=" + code + "&page=1&per=30";

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Referer", "https://fund.eastmoney.com/")
                    .timeout(10000)
                    .get();

            Elements trs = doc.select("table tbody tr");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Element tr : trs) {
                Elements tds = tr.select("td");
                if (tds.size() < 2) continue;

                String dateStr = tds.get(0).text().trim();
                String netStr = tds.get(1).text().trim();

                try {
                    LocalDate date = LocalDate.parse(dateStr, fmt);
                    double net = Double.parseDouble(netStr);

                    FundData data = new FundData();
                    data.setCode(code);
                    data.setName(getFundName(code));
                    data.setDate(date);
                    data.setNetValue(net);
                    list.add(data);
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            System.out.println(code + " 爬取失败");
        }
        return list;
    }

    // ✅ 修复：正确获取基金名称
    private String getFundName(String code) {
        try {
            Document doc = Jsoup.connect("https://fund.eastmoney.com/" + code)
                    .userAgent(USER_AGENT)
                    .timeout(8000)
                    .get();
            Element nameEle = doc.selectFirst(".fundName");
            if (nameEle != null) return nameEle.text();
        } catch (Exception e) {
            return "基金" + code;
        }
        return "基金" + code;
    }

    // 间隔2秒
    public void sleep() {
        try {
            Thread.sleep(CRAWL_INTERVAL_SECONDS * 1000L);
        } catch (InterruptedException ignored) {}
    }
}
