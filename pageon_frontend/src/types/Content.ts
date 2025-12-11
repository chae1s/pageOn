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
    keywords: UserKeywordResponse[];
    episodeCount: number;
    totalAverageRating: number;
    totalRatingCount: number;
    contentType: string;
}

export interface LibraryContent {
    id: number;
    title: string;
    author: string;
    cover: string;
    contentType: string;
    episodeId: number;
    lastReadAt: string;

}

