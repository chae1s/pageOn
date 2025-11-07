export interface Comment {
    id: number;
    bookTitle: string;
    bookCover: string;
    content: string;
    episodeNum: number;
    nickname: string;
    date: string;
    likes: number;
}

export interface CreateComment {
    text: string;
    isSpoiler: boolean;
}

export interface EpisodeComment {
    id: number;
    text: string;
    episodeNum: number;
    contentTitle: string;
    nickname: string;
    createdAt: string;
    isSpoiler: boolean;
    isMine: boolean;
    likeCount: number;
}

export interface MyComment {
    id: number;
    text: string;
    episodeId: number;
    episodeNum: number;
    contentId: number;
    contentTitle: number;
    createdAt: string;
    likeCount: number;
}