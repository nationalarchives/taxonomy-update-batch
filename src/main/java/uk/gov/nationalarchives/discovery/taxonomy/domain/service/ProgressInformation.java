package uk.gov.nationalarchives.discovery.taxonomy.domain.service;

/**
 * Created by jcharlet on 8/15/16.
 */
public class ProgressInformation {
    int totalNbOfIaViews = 0;
    int nbOfProcessedItems = 0;
    int percentageOfProcessedItems = 0;

    public ProgressInformation(int totalNbOfIaViews) {
        this.totalNbOfIaViews = totalNbOfIaViews;
    }

    public int getTotalNbOfIaViews() {
        return totalNbOfIaViews;
    }

    public void setTotalNbOfIaViews(int totalNbOfIaViews) {
        this.totalNbOfIaViews = totalNbOfIaViews;
    }

    public int getNbOfProcessedItems() {
        return nbOfProcessedItems;
    }

    public void setNbOfProcessedItems(int nbOfProcessedItems) {
        this.nbOfProcessedItems = nbOfProcessedItems;
    }

    public void addToNbOfProcessedItems(int nbOfProcessedItems) {
        this.nbOfProcessedItems += nbOfProcessedItems;
    }

    public int getPercentageOfProcessedItems() {
        return percentageOfProcessedItems;
    }

    public void setPercentageOfProcessedItems(int percentageOfProcessedItems) {
        this.percentageOfProcessedItems = percentageOfProcessedItems;
    }
}
