package ua.kovalchuk.googleadsintegration;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v10.services.GoogleAdsRow;
import com.google.ads.googleads.v10.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v10.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.google.ads.googleads.v10.services.SearchGoogleAdsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoogleCommandLineRunner implements CommandLineRunner {

    private static final int PAGE_SIZE = 100;

    @Override
    public void run(String... args) throws Exception {
        log.info("\uD83D\uDD25 Run GoogleCommandLineRunner");


        GoogleAdsClient googleAdsClient = GoogleAdsClient.newBuilder()
            .fromProperties(getAdsProperties())
            .build();

        runExample(googleAdsClient, )
    }

    private void runExample(GoogleAdsClient googleAdsClient, long customerId, long labelId) {
        try (GoogleAdsServiceClient googleAdsServiceClient =
                 googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {
            // Creates a request that will retrieve all campaign labels with the specified
            // labelId using pages of the specified page size.
            SearchGoogleAdsRequest request =
                SearchGoogleAdsRequest.newBuilder()
                    .setCustomerId(Long.toString(customerId))
                    .setPageSize(PAGE_SIZE)
                    .setQuery(
                        "SELECT campaign.id, campaign.name, label.id, label.name "
                            + "FROM campaign_label WHERE label.id = "
                            + labelId
                            + " ORDER BY campaign.id")
                    .build();
            // Issues the search request.
            SearchPagedResponse searchPagedResponse = googleAdsServiceClient.search(request);
            // Checks if the total results count is greater than 0.
            if (searchPagedResponse.getPage().getResponse().getTotalResultsCount() > 0) {
                // Iterates over all rows in all pages and prints the requested field values for the
                // campaigns and labels in each row. The results include the campaign and label
                // objects because these were included in the search criteria.
                for (GoogleAdsRow googleAdsRow : searchPagedResponse.iterateAll()) {
                    System.out.printf(
                        "Campaign found with name '%s', ID %d, and label: %s.%n",
                        googleAdsRow.getCampaign().getName(),
                        googleAdsRow.getCampaign().getId(),
                        googleAdsRow.getLabel().getName());
                }
            } else {
                System.out.println("No campaigns were found.");
            }
        }
    }

}
