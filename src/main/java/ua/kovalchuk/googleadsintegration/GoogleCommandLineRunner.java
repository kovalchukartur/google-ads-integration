package ua.kovalchuk.googleadsintegration;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v10.services.GoogleAdsRow;
import com.google.ads.googleads.v10.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v10.services.SearchGoogleAdsRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoogleCommandLineRunner implements CommandLineRunner {

    private static final String ADS_PROPERTIES_PATH = "ads.properties";
    private static final String NEW_METRIC_QUERY2 = "SELECT metrics.biddable_app_install_conversions, asset.name, asset.id, campaign.id, campaign.name, ad_group.campaign, segments.date FROM ad_group_ad_asset_view WHERE asset.id = 10067150534 AND segments.date = '2022-04-23'";

    @Override
    public void run(String... args) {
        log.info("\uD83D\uDD25 Run GoogleCommandLineRunner");

        Properties properties = getAdsProperties();

        GoogleAdsClient googleAdsClient = GoogleAdsClient.newBuilder()
            .fromProperties(properties)
            .build();

        runExample(googleAdsClient);
    }

    @SneakyThrows
    private Properties getAdsProperties() {
        final InputStream propertiesInputStream = new ClassPathResource(ADS_PROPERTIES_PATH).getInputStream();
        final Properties properties = new Properties();
        properties.load(propertiesInputStream);
        return properties;
    }

    private void runExample(GoogleAdsClient googleAdsClient) {
        try (GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getVersion10().createGoogleAdsServiceClient()) {
            SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                .setCustomerId(Long.toString(7959147474L))
                .setQuery(NEW_METRIC_QUERY2)
                .build();
            GoogleAdsServiceClient.SearchPagedResponse searchPagedResponse = googleAdsServiceClient.search(request);

            Collection<GoogleAdsRow> rows = getAllAdsRows(searchPagedResponse);
            if (rows.isEmpty()) {
                log.info("❌ Not found");
            } else {
                log.info("☀️ Found {} rows", rows.size());

                rows.forEach(row -> {
                    log.info("Row = \n {}", row.toString());
                    log.info("Metric = {}", row.getMetrics().getBiddableAppInstallConversions());
                });
            }
        }
    }

    private Collection<GoogleAdsRow> getAllAdsRows(final GoogleAdsServiceClient.SearchPagedResponse response) {
        log.info("Getting all Ads rows from the response: {}", response);
        final Collection<GoogleAdsRow> googleAdsRows = new ArrayList<>();
        response.iterateAll().forEach(googleAdsRows::add);
        return googleAdsRows;
    }

}
