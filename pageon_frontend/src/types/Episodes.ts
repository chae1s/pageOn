import { WebtoonImagesResponse } from "./WebtoonImage";

export interface EpisodeSummary {
    id: number;
    episodeNum: number;
    episodeTitle: string;
    createdAt: string;
    purchasePrice: number;
    rentalPrice: number;
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
}