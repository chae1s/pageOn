import { EpisodeSummary } from "./Episodes";
import { UserKeywordResponse } from "./Keyword";

export type ContentType = 'WEBNOVEL' | 'WEBTOON';
export type ContentStatus = 'COMPLETED' | 'ONGOING' | 'REST';

export interface SimpleContent {
    id: number;
    title: string;
    author: string;
    cover: string;
    contentType: string;
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
    id: number;
    title: string;
    description: string;
    cover: string;
    author: string;
    keywords: UserKeywordResponse[];
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
    id: number;
    title: string;
    description: string;
    cover: string;
    author: string;
    episodeCount: number;
    episodeUpdatedAt: string;
    keywords: UserKeywordResponse[];
    totalAverageRating: number;
    totalRatingCount: number;
    contentType: string;
}

export interface InterestContent {
    contentId: number;
    title: string;
    penName: string;
    episodeUpdatedAt: string;
    cover: string;
    contentType: string;
    serialDay: string;
    status: ContentStatus;
}

export interface RecentReadContent {
    contentId: number;
    title: string;
    penName: string;
    cover: string;
    episodeUpdatedAt: string;
    lastReadAt: string;
    lastReadEpisodeId: number;
    contentType: string;
    serialDay: string;
    status: string;
}

