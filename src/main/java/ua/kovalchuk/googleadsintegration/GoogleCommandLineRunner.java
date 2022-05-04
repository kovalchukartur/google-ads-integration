package ua.kovalchuk.googleadsintegration;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v10.services.GoogleAdsRow;
import com.google.ads.googleads.v10.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v10.services.SearchGoogleAdsRequest;
import com.google.ads.googleads.v10.services.SearchGoogleAdsResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public static final String SELECT_METRICS = "SELECT ad_group_ad_asset_view.asset FROM ad_group_ad_asset_view";
    private static final String SELECT_CAMPAIGNS_SETTINGS_QUERY =
        "SELECT " +
            "campaign.id  " +
            "FROM campaign " +
            "ORDER BY campaign.name ASC";
    private static final String ASSETS_QUERY = "SELECT asset.name, asset.youtube_video_asset.youtube_video_id, asset.type " +
        "FROM ad_group_ad_asset_view " +
        "WHERE asset.type IN ('IMAGE','YOUTUBE_VIDEO')";


    // SELECT metrics.biddable_app_install_conversions, asset.name, asset.id FROM ad_group_ad_asset_view WHERE asset.id = 10067150534 AND campaign.name = 'SGH_UA_FiB_UAC_And_US_tCPA_Level20D1_All_010921_DAU_T1Boost'
    private static final String NEW_METRIC_QUERY2 = "SELECT metrics.biddable_app_install_conversions, asset.name, asset.id, campaign.id, campaign.name, ad_group.campaign, segments.date FROM ad_group_ad_asset_view WHERE asset.id = 10067150534 AND segments.date = '2022-04-23' "
//        "WHERE asset.id = 10067150534 AND campaign.name = 'SGH_UA_FiB_UAC_And_US_tCPA_Level20D1_All_010921_DAU_T1Boost'"
        ;

    @Override
    public void run(String... args) throws Exception {
        log.info("\uD83D\uDD25 Run GoogleCommandLineRunner");


        Properties properties = getAdsProperties();

        GoogleAdsClient googleAdsClient = GoogleAdsClient.newBuilder()
            .fromProperties(properties)
            .build();

//        List<Long> ids = List.of(9698763824L, 3278187245L, 8830760741L, 6356221986L, 9672356800L, 6514451218L,
//            2871536275L, 3415456598L, 7271089169L, 6922822425L, 9069378135L, 8267743739L,
//            7959147474L, 2621041619L, 8251777820L, 7697258431L, 4044324766L, 6183556839L,
//            6658130250L, 5506971650L, 3661899537L, 8527590557L, 2203417545L, 5311939122L,
//            4288141394L);

        // 3070278717 -
        // 4318958800 -
        // 9122208542 +
        // 6985616895 -
        // 7959147474 ?

        List<Long> ids = List.of(
//            3070278717L,
//            4318958800L,
            9122208542L,
//            6985616895L,
            7959147474L
        );

        ids.forEach(id -> runExample(googleAdsClient, id));
    }

    @SneakyThrows
    private Properties getAdsProperties() {
        final InputStream propertiesInputStream = new ClassPathResource(ADS_PROPERTIES_PATH).getInputStream();
        final Properties properties = new Properties();
        properties.load(propertiesInputStream);
        return properties;
    }

    private void runExample(GoogleAdsClient googleAdsClient, long customerId) {
        try (GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getVersion10().createGoogleAdsServiceClient()) {
            // Creates a request that will retrieve all campaign labels with the specified
            // labelId using pages of the specified page size.
            SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                .setCustomerId(Long.toString(customerId))
                .setQuery(NEW_METRIC_QUERY2)
                .build();
            // Issues the search request.
            GoogleAdsServiceClient.SearchPagedResponse searchPagedResponse = googleAdsServiceClient.search(request);
            // Checks if the total results count is greater than 0.

            Collection<GoogleAdsRow> rows = getAllAdsRows(searchPagedResponse);
            if (rows.isEmpty()) {
                log.info("\uD83E\uDD8B Not found");
            } else {
                log.info("☀️ Found {} rows", rows.size());
            }

            SearchGoogleAdsResponse response = searchPagedResponse.getPage().getResponse();
            if (response.getTotalResultsCount() > 0) {
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

    private Collection<GoogleAdsRow> getAllAdsRows(final GoogleAdsServiceClient.SearchPagedResponse response) {
        log.info("Getting all Ads rows from the response: {}", response);
        final Collection<GoogleAdsRow> googleAdsRows = new ArrayList<>();
        response.iterateAll().forEach(googleAdsRows::add);
        return googleAdsRows;
    }

}
