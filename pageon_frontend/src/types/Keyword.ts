export interface Keyword {
    categoryId: number;
    name: string;
}

export interface KeywordListItem {
    id: number;
    name: string;
}

export interface Category {
    id: number;
    name: string;
    keywords: KeywordListItem[];
}