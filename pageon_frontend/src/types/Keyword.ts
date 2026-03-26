export interface Keyword {
    id: number;
    name: string;
}


export interface Category {
    id: number;
    name: string;
    keywords: Keyword[];
}