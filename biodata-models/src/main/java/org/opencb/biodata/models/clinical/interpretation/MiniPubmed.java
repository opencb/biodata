package org.opencb.biodata.models.clinical.interpretation;

public class MiniPubmed {

    private String id;
    private String name;
    private String journal;
    private String summary;
    private String date;
    private String url;

    public MiniPubmed() {
    }

    public MiniPubmed(String id, String name, String summary, String date, String url, String journal) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.date = date;
        this.url = url;
        this.journal = journal;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MiniPubmed{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", summary='").append(summary).append('\'');
        sb.append(", date=").append(date);
        sb.append(", url='").append(url).append('\'');
        sb.append(", journal='").append(journal).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public MiniPubmed setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MiniPubmed setName(String name) {
        this.name = name;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public MiniPubmed setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public String getDate() {
        return date;
    }

    public MiniPubmed setDate(String date) {
        this.date = date;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public MiniPubmed setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getJournal() {
        return journal;
    }

    public MiniPubmed setJournal(String journal) {
        this.journal = journal;
        return this;
    }
}
