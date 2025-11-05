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