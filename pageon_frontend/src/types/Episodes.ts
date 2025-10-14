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
    prevEpisodeId: number | null;
    nextEpisodeId: number | null;
}