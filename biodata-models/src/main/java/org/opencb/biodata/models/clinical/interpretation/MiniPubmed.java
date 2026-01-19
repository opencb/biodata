package org.opencb.biodata.models.clinical.interpretation;

import java.util.List;

public class MiniPubmed {

    private String id;
    private String title;
    private String journal;
    private String summary;
    private List<String> authors;
    private String date;
    private String url;

    public MiniPubmed() {
    }

    @Deprecated
    public MiniPubmed(String id, String title, String summary, String date, String url, String journal) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.date = date;
        this.url = url;
        this.journal = journal;
    }

    public MiniPubmed(String id, String title, String journal, String summary, List<String> authors, String date, String url) {
        this.id = id;
        this.title = title;
        this.journal = journal;
        this.summary = summary;
        this.authors = authors;
        this.date = date;
        this.url = url;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MiniPubmed{");
        sb.append("id='").append(id).append('\'');

        sb.append(", title='").append(title).append('\'');
        sb.append(", journal='").append(journal).append('\'');
        sb.append(", summary='").append(summary).append('\'');
        sb.append(", authors=").append(authors);
        sb.append(", date='").append(date).append('\'');
        sb.append(", url='").append(url).append('\'');
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

    @Deprecated
    public String getName() {
        return title;
    }

    @Deprecated
    public MiniPubmed setName(String name) {
        this.title = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MiniPubmed setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public MiniPubmed setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public MiniPubmed setAuthors(List<String> authors) {
        this.authors = authors;
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
