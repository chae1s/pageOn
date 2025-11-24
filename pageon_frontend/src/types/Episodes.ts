import { BestComment } from "./Comments";
import { WebtoonImagesResponse } from "./WebtoonImage";

export interface EpisodeSummary {
    id: number;
    episodeNum: number;
    episodeTitle: string;
    createdAt: string;
    purchasePrice: number;
    rentalPrice: number;
    episodePurchase: EpisodePurchase | null;
}

export interface WebnovelEpisodeDetail {
    id: number;
    title: string;
    episodeNum: number;
    episodeTitle: string;
    content: string;
    averageRating: number;
    ratingCount: number;
    prevEpisodeId: number | null;
    nextEpisodeId: number | null;
    userScore: number | null;
    bestComment: BestComment | null;
}

export interface WebtoonEpisodeDetail {
    id: number;
    title: string;
    episodeNum: number;
    averageRating: number;
    ratingCount: number
    images: WebtoonImagesResponse[];
    prevEpisodeId: number | null;
    nextEpisodeId: number | null;
    userScore: number | null;
    bestComment: BestComment | null;
}

export interface EpisodePurchase {
    id: number;
    purchaseType: string;
    expiredAt: string;
}