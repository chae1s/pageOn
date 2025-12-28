import { EpisodeSummary } from "./Episodes";
import { Keyword } from "./Keyword";

export type ContentType = 'WEBNOVEL' | 'WEBTOON';
export type ContentStatus = 'COMPLETED' | 'ONGOING' | 'REST';

export interface SimpleContent {
    contentId: number;
    contentTitle: string;
    author: string;
    cover: string;
    contentType: string;
    totalAverageRating: number;
    totalRatingCount: number;
}

export interface RankingBook {
    id: number;
    title: string;
    author: string;
    cover: string;
    rating: number;
    ratingCount: number;
    contentType: string;
}

export interface ContentDetail {
    contentId: number;
    contentTitle: string;
    description: string;
    cover: string;
    author: string;
    keywords: Keyword[];
    episodes: EpisodeSummary[];
    serialDay: string;
    totalAverageRating: number;
    totalRatingCount: number;
    status: ContentStatus;
    viewCount: number;
    contentType: string;
    isInterested: boolean;
}

export interface SearchContent {
    contentId: number;
    contentTitle: string;
    description: string;
    cover: string;
    author: string;
    episodeCount: number;
    episodeUpdatedAt: string;
    keywords: Keyword[];
    totalAverageRating: number;
    totalRatingCount: number;
    contentType: string;
}

export interface InterestContent {
    contentId: number;
    contentTitle: string;
    author: string;
    episodeUpdatedAt: string;
    cover: string;
    contentType: string;
    serialDay: string;
    status: ContentStatus;
}

export interface RecentReadContent {
    contentId: number;
    contentTitle: string;
    author: string;
    cover: string;
    episodeUpdatedAt: string;
    lastReadAt: string;
    lastReadEpisodeId: number;
    contentType: string;
    serialDay: string;
    status: string;
}

