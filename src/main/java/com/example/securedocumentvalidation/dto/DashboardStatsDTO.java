package com.example.securedocumentvalidation.dto;

public class DashboardStatsDTO {

    private long totalUsers;
    private long totalDocuments;
    private long totalUploads;
    private long totalDownloads;
    private long totalDeniedAccess;
    private long totalDeletedDocuments;

    public DashboardStatsDTO(
            long totalUsers,
            long totalDocuments,
            long totalUploads,
            long totalDownloads,
            long totalDeniedAccess,
            long totalDeletedDocuments) {

        this.totalUsers = totalUsers;
        this.totalDocuments = totalDocuments;
        this.totalUploads = totalUploads;
        this.totalDownloads = totalDownloads;
        this.totalDeniedAccess = totalDeniedAccess;
        this.totalDeletedDocuments = totalDeletedDocuments;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalDocuments() {
        return totalDocuments;
    }

    public long getTotalUploads() {
        return totalUploads;
    }

    public long getTotalDownloads() {
        return totalDownloads;
    }

    public long getTotalDeniedAccess() {
        return totalDeniedAccess;
    }

    public long getTotalDeletedDocuments() {
        return totalDeletedDocuments;
    }
}