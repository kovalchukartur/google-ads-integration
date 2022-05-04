package ua.kovalchuk.googleadsintegration;

import com.google.ads.googleads.lib.GoogleAdsClient;
//import com.google.ads.googleads.v10.services.GoogleAdsRow;
//import com.google.ads.googleads.v10.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.google.ads.googleads.v10.services.SearchGoogleAdsRequest;
//import com.google.ads.googleads.v10.services.SearchGoogleAdsResponse;
import com.google.ads.googleads.v9.services.GoogleAdsRow;
import com.google.ads.googleads.v9.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v9.services.SearchGoogleAdsResponse;
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
    // 3070278717 -
    // 4318958800 -
    // 9122208542 +
    // 6985616895 -
    // 7959147474 ?

    public static final String SELECT_METRICS = "SELECT ad_group_ad_asset_view.asset FROM ad_group_ad_asset_view";
    // 9698763824 3278187245 8830760741 6356221986 9672356800 6514451218
    // 2871536275 3415456598 7271089169 6922822425 9069378135 8267743739
    // 7959147474 2621041619 8251777820 7697258431 4044324766 6183556839
    // 6658130250 5506971650 3661899537 8527590557 2203417545 5311939122
    // 4288141394
    private static final long CUSTOMER_ID = 9122208542L; // account id
    private static final String SELECT_CAMPAIGNS_SETTINGS_QUERY =
        "SELECT " +
            "campaign.id  " +
            "FROM campaign " +
            "ORDER BY campaign.name ASC";
    private static final String ASSETS_QUERY = "SELECT asset.name, asset.youtube_video_asset.youtube_video_id, asset.type " +
        "FROM ad_group_ad_asset_view " +
        "WHERE asset.type IN ('IMAGE','YOUTUBE_VIDEO')";

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

        List<Long> ids = List.of(8267743739L);

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
        try (com.google.ads.googleads.v9.services.GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getVersion9().createGoogleAdsServiceClient()) {
            // Creates a request that will retrieve all campaign labels with the specified
            // labelId using pages of the specified page size.
            com.google.ads.googleads.v9.services.SearchGoogleAdsRequest request = com.google.ads.googleads.v9.services.SearchGoogleAdsRequest.newBuilder()
                .setCustomerId(Long.toString(customerId))
                .setQuery(ASSETS_QUERY)
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
