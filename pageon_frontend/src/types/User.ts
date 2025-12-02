
// 로그인
export interface LoginRequest {
    email: string;
    password: string;
}

// 회원가입
export interface SignupRequest {
    email: string;
    password: string;
    confirmPassword: string;
    nickname: string;
    termsAgreed: boolean;
    birthDate: string;
    gender: string;
}

// 간단한 회원 정보
export interface UserSimpleProfile {
    id: number;
    nickname: string;
    pointBalance: number;
}


// 회원정보
export interface UserProfile {
    id: number;
    email: string;
    nickname: string;
    pointBalance: number;
    birthDate: string
    oauthProvider: string;
}

// 회원정보 수정
export interface UpdateRequest {
    nickname: string;
    password: string;
    confirmPassword: string;
}

// 탈퇴 정보
export interface DeleteRequest {
    password: string;
    reasonIndex: number;
    reason: string;
    otherReason: string;
}

export interface PointTransaction {
    createdAt: string;
    description: string;
    amount: number;
    balance: number;
}