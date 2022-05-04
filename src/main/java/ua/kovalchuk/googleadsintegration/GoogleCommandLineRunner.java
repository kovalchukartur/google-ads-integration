package ua.kovalchuk.googleadsintegration;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v10.services.GoogleAdsRow;
import com.google.ads.googleads.v10.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v10.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.google.ads.googleads.v10.services.SearchGoogleAdsRequest;
import java.io.InputStream;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoogleCommandLineRunner implements CommandLineRunner {

    private static final int PAGE_SIZE = 100;
    private static final String ADS_PROPERTIES_PATH = "ads.properties";
    // 3070278717 -
    // 4318958800 -
    // 9122208542 +
    // 6985616895 -
    private static final long CUSTOMER_ID = 9122208542L; // account id

    @Override
    public void run(String... args) throws Exception {
        log.info("\uD83D\uDD25 Run GoogleCommandLineRunner");


        GoogleAdsClient googleAdsClient = GoogleAdsClient.newBuilder()
            .fromProperties(getAdsProperties())
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
        try (GoogleAdsServiceClient googleAdsServiceClient =
                 googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {
            // Creates a request that will retrieve all campaign labels with the specified
            // labelId using pages of the specified page size.
            SearchGoogleAdsRequest request =
                SearchGoogleAdsRequest.newBuilder()
                    .setCustomerId(Long.toString(CUSTOMER_ID))
                    .setPageSize(PAGE_SIZE)
                    .setQuery(
                        "SELECT metrics.biddable_app_install_conversions\n" +
                            "FROM ad_group_ad_asset_view\n" +
                            "WHERE\n" +
                            "      campaign.name = 'SGH_UA_FiB_UAC_And_US_tCPA_Level20D1_All_010921_DAU_T1Boost'\n"
//                            "  AND campaign.start_date = '2022-04-23' AND campaign.end_date = '2022-04-23'\n" +
//                            "  AND asset.id = 10067150534"

                    )
                    .build();
            // Issues the search request.
            SearchPagedResponse searchPagedResponse = googleAdsServiceClient.search(request);
            // Checks if the total results count is greater than 0.
            if (searchPagedResponse.getPage().getResponse().getTotalResultsCount() > 0) {
                // Iterates over all rows in all pages and prints the requested field values for the
                // campaigns and labels in each row. The results include the campaign and label
                // objects because these were included in the search criteria.
                for (GoogleAdsRow googleAdsRow : searchPagedResponse.iterateAll()) {
                   log.info(
                        "Campaign found with name '{}', ID {}, and label: {}",
                        googleAdsRow.getCampaign().getName(),
                        googleAdsRow.getCampaign().getId(),
                        googleAdsRow.getLabel().getName());
                }
            } else {
                log.info("No campaigns were found.");
            }
        }
    }

}
