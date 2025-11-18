import React, { useState, useEffect, useCallback } from "react";
import { SortBtn } from "../../styles/Layout.styles";
import * as C from "./Comment.styles";
import * as S from "./Viewer.styles";
import closeIcon from "../../assets/crossIcon.png";
import { useLocation, useNavigate, useParams, useSearchParams } from "react-router-dom";
import api from "../../api/axiosInstance";
import { CreateComment, EpisodeComment } from "../../types/Comments";
import LikeEmptyIcon from "../../assets/emptyHeartIcon.png";
import LikeFullIcon from "../../assets/fullHeartIcon.png";
import BlankCheckboxIcon from "../../assets/blankCheckboxIcon.png";
import FullCheckboxIcon from "../../assets/fullCheckboxIcon.png";
import { Pagination } from "../../types/Page";
import { formatDate } from "../../utils/formatDate";


function EpisodeCommentsPage() {
    const { contentType, contentId, episodeId } = useParams<{contentType: string; contentId: string; episodeId: string}>();
    const [searchParams, setSearchParams] = useSearchParams();

    const [commentText, setCommentText] = useState<string>("")
    const [isSpoiler, setIsSpoiler] = useState<boolean>(false)

    const sort = searchParams.get("sort") || "popular";
    const page = parseInt(searchParams.get("page") || "0", 10);
    
    const [episodeComments, setEpisodeComments] = useState<EpisodeComment[]>([]);
    const [pageData, setPageData] = useState<Pagination<EpisodeComment> | null>(null);
    const [revealedSpoilers, setRevealedSpoilers] = useState<Set<number>>(new Set());
    const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
    const [editingCommentText, setEditingCommentText] = useState<string>("");
    const [editingCommentSpoiler, setEditingCommentSpoiler] = useState<boolean>(false);

    const navigate = useNavigate();

    const handleClose = (e:React.MouseEvent<HTMLAnchorElement>) => {
        e.preventDefault();
        const viewerUrl = `/${contentType}/${contentId}/viewer/${episodeId}`

        navigate(viewerUrl);
    };

    const getComments = useCallback(async () => {
        try {
            const response = await api.get(`/${contentType}/episodes/${episodeId}/comments`, {
                params: {
                    sort: sort, 
                    page: page,
                }
            });
            
            console.log(response.data.content);
            setEpisodeComments(response.data.content ?? []);
            setPageData(response.data);

        } catch (error) {
            console.error("에피소드 댓글 조회 실패: ", error);
        }
    }, [sort, page]);

    useEffect(() => {
        getComments();

    }, [getComments])

    const NextIcon = () => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M8 5l4 5-4 5" stroke="#222" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
    )

    const PrevIcon = () => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M12 5l-4 5 4 5" stroke="#222" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
    )

    const handleRegister = async () => {
        const text = commentText.trim()

        
        const newComment: CreateComment = {
            text: text,
            isSpoiler: isSpoiler
        }

        console.log(newComment)

        try {
            await api.post(`/${contentType}/episodes/${episodeId}/comments`, newComment);
                
            setCommentText("");
            setIsSpoiler(false);

            getComments();
        } catch (error) {
            console.error("댓글 등록 실패 : ", error);
        }
        
    }

    const toggleSpoilerReveal = (commentId: number) => {
        setRevealedSpoilers(prev => {
            const next = new Set(prev);
            if (next.has(commentId)) {
                next.delete(commentId);
            } else {
                next.add(commentId);
            }
            return next;
        });
    }

    const handleParamClick = (newKey: string, newValue: string) => {
        const newParams = new URLSearchParams(searchParams);

        newParams.set(newKey, newValue);
        newParams.set("page", "0");

        setSearchParams(newParams);
    }

    const handlePageChange = (newPage: number) => {
        const newParams = new URLSearchParams(searchParams);
        newParams.set("page", newPage.toString());
        setSearchParams(newParams);
    }

    const handleEditClick = (comment: EpisodeComment) => {
        setEditingCommentId(comment.id);
        setEditingCommentText(comment.text);
        setEditingCommentSpoiler(comment.isSpoiler);
    }

    const handleCancelEdit = () => {
        setEditingCommentId(null);
        setEditingCommentText("");
        setEditingCommentSpoiler(false);
    }

    const handleUpdateComment = async (commentId: number) => {
        const text = editingCommentText.trim();
        
        if (!text) {
            return;
        }

        const updateComment: CreateComment = {
            text: text,
            isSpoiler: editingCommentSpoiler
        }

        try {
            await api.patch(`/${contentType}/comments/${commentId}`, updateComment);
            
            setEditingCommentId(null);
            setEditingCommentText("");
            setEditingCommentSpoiler(false);

            getComments();
        } catch (error) {
            console.error("댓글 수정 실패 : ", error);
        }
    }

    const handleDeleteComment = async (commentId: number) => {
        if (!window.confirm("댓글을 삭제하시겠습니까?")) {
            return;
        }
        
        
        try {
            await api.delete(`${contentType}/comments/${commentId}`)

            getComments();

        } catch (error) {
            console.error("댓글 삭제 실패: ", error);
        }
    }

    const getPageNumbers = () => {
        if (!pageData) return [];

        const currentPage = pageData.pageNumber;
        const totalPages = pageData.totalPages;

        // 한 번에 보여줄 페이지 번호 개수
        const pageBlockSize = 6;

        const startPage = Math.floor(currentPage / pageBlockSize) * pageBlockSize;

        let endPage = startPage + pageBlockSize - 1;

        if (endPage >= totalPages) {
            endPage = totalPages - 1;
        }

        const pages = [];
        for (let i = startPage; i <= endPage; i++) {
            pages.push(i)
        }

        return pages;
    }

    const pageNumbers = getPageNumbers();

    const handleLikeComment = async (commentId: number) => {
        try {
            await api.post(`/${contentType}/comments/${commentId}/likes`)

            setEpisodeComments((prevComments) =>
                prevComments.map((comment) =>
                    comment.id === commentId
                        ? comment.isLiked
                            ? comment
                            : { ...comment, likeCount: comment.likeCount + 1, isLiked: true }
                        : comment
                )
            );
        } catch (error) {
            console.error("댓글 좋아요 실패: ", error);
        }
    }
      
    if (!contentType || !episodeId || !contentId) {
        return null;
    }
    return (
        <S.Viewer>
            <S.CommentPageHeaderSection>
                <S.CommentPageHeader>
                    <S.CommentPageCloseLink to="/" onClick={handleClose}>
                        <S.CommentPageCloseIcon src={closeIcon}/>
                    </S.CommentPageCloseLink>
                </S.CommentPageHeader>
            </S.CommentPageHeaderSection>
            <S.ViewerCommentSection>
                <C.CommentList>
                <C.CommentHeader>
                        <C.CommentCount>
                            댓글 {episodeComments.length}개
                        </C.CommentCount>
                        <C.SortBtnList>
                            <SortBtn $active={sort === "popular"} type="button" onClick={() => handleParamClick("sort", "popular")}>좋아요순</SortBtn>
                            <SortBtn $active={sort === "latest"} type="button" onClick={() => handleParamClick("sort", "latest")}>최신순</SortBtn>
                        </C.SortBtnList>
                    </C.CommentHeader>
                    <C.CommentInputSection>
                        <C.CommentInputWrap>
                            <C.CommentInputFlex>
                                <C.CommentInputTextarea
                                    value={commentText}
                                    onChange={(e)=>setCommentText(e.target.value)}
                                    onInput={(e)=>{
                                        const el = e.currentTarget;
                                        el.style.height = '18px';
                                        el.style.height = `${el.scrollHeight}px`;
                                    }}
                                    rows={1}
                                >
                                </C.CommentInputTextarea>
                            </C.CommentInputFlex>
                            {commentText.trim().length > 0 && (
                                <C.CommentInputBtn onClick={handleRegister}>
                                    등록
                                </C.CommentInputBtn>
                            )}
                        </C.CommentInputWrap>
                        <C.CommentSpoilerCheckSection>
                            <C.CommentSpoilerCheckWrap>
                                <C.CommentSpoilerCheckboxWrap onClick={() => setIsSpoiler(prev => !prev)}>
                                    <C.CommentSpoilerCheckbox
                                        type="checkbox"
                                        checked={isSpoiler}
                                        onChange={(e)=>setIsSpoiler(e.target.checked)}
                                        style={{ display: 'none' }}
                                    />
                                    {isSpoiler ? (
                                        <C.CommentSpoilerCheckboxCheckIcon src={FullCheckboxIcon} />
                                    ) : (
                                        <C.CommentSpoilerCheckboxEmptyIcon src={BlankCheckboxIcon} />
                                    )}
                                </C.CommentSpoilerCheckboxWrap>
                                <C.CommentSpoilerText onClick={() => setIsSpoiler(prev => !prev)}>
                                    댓글에 스포일러 포함
                                </C.CommentSpoilerText>
                            </C.CommentSpoilerCheckWrap>
                        </C.CommentSpoilerCheckSection>
                    </C.CommentInputSection>
                    <C.CommentListUl>
                        {episodeComments.length === 0 ? (
                            <C.CommentListEmptyText>댓글이 없습니다.</C.CommentListEmptyText>
                        ) : (
                            episodeComments.map((comment) => (
                                <C.CommentListLi key={comment.id}>
                                    <C.CommentUserInfo>
                                        <div>
                                            {comment.nickname}
                                        </div>
                                    </C.CommentUserInfo>
                                    {editingCommentId === comment.id ? (
                                        <>
                                            <C.CommentContentWrap>
                                                <C.CommentInputWrap style={{ border: '1px solid #ccc', borderRadius: '8px', padding: '10px', marginBottom: '10px' }}>
                                                    <C.CommentInputFlex>
                                                        <C.CommentInputTextarea
                                                            value={editingCommentText}
                                                            onChange={(e) => setEditingCommentText(e.target.value)}
                                                            onInput={(e) => {
                                                                const el = e.currentTarget;
                                                                el.style.height = '18px';
                                                                el.style.height = `${el.scrollHeight}px`;
                                                            }}
                                                            rows={1}
                                                            style={{ border: 'none', padding: 0, background: 'transparent' }}
                                                        />
                                                    </C.CommentInputFlex>
                                                    {editingCommentText.trim().length > 0 && (
                                                        <C.CommentInputBtn onClick={() => handleUpdateComment(comment.id)}>
                                                            저장
                                                        </C.CommentInputBtn>
                                                    )}
                                                </C.CommentInputWrap>
                                                <C.CommentSpoilerCheckSection>
                                                    <C.CommentSpoilerCheckWrap>
                                                        <C.CommentSpoilerCheckboxWrap onClick={() => setEditingCommentSpoiler(prev => !prev)}>
                                                            <C.CommentSpoilerCheckbox
                                                                type="checkbox"
                                                                checked={editingCommentSpoiler}
                                                                onChange={(e) => setEditingCommentSpoiler(e.target.checked)}
                                                                style={{ display: 'none' }}
                                                            />
                                                            {editingCommentSpoiler ? (
                                                                <C.CommentSpoilerCheckboxCheckIcon src={FullCheckboxIcon} />
                                                            ) : (
                                                                <C.CommentSpoilerCheckboxEmptyIcon src={BlankCheckboxIcon} />
                                                            )}
                                                        </C.CommentSpoilerCheckboxWrap>
                                                        <C.CommentSpoilerText onClick={() => setEditingCommentSpoiler(prev => !prev)}>
                                                            댓글에 스포일러 포함
                                                        </C.CommentSpoilerText>
                                                    </C.CommentSpoilerCheckWrap>
                                                </C.CommentSpoilerCheckSection>
                                            </C.CommentContentWrap>
                                            <C.CommentInfo>
                                                <C.CommentInfoLeft>
                                                    <C.CommentEpisode>
                                                        <C.CommentTitle>{comment.contentTitle}</C.CommentTitle>
                                                        <div>{comment.episodeNum}화</div>
                                                    </C.CommentEpisode>
                                                    <C.CommentDateBtn>
                                                        <div>{formatDate(comment.createdAt)}</div>
                                                        <C.CommentSpace></C.CommentSpace>
                                                        <C.CommentLeftBtn onClick={handleCancelEdit}>취소</C.CommentLeftBtn>
                                                    </C.CommentDateBtn>  
                                                </C.CommentInfoLeft>
                                                <div>
                                                    <C.CommentLikeBtn type="button" onClick={() => handleLikeComment(comment.id)}>
                                                        <C.LikeEmptyIcon src={LikeEmptyIcon} />
                                                        <span>{comment.likeCount}</span>
                                                    </C.CommentLikeBtn>
                                                </div>
                                            </C.CommentInfo>
                                        </>
                                    ) : (
                                        <>
                                            <C.CommentContentWrap>
                                                {comment.isSpoiler && !revealedSpoilers.has(comment.id) ? (
                                                    <>
                                                        <C.HiddenCommentContent>{comment.text}</C.HiddenCommentContent>
                                                        <C.CommentSpoilerOverlay onClick={() => toggleSpoilerReveal(comment.id)}>
                                                            <C.CommentSpoiler>스포일러가 포함된 댓글입니다.</C.CommentSpoiler>
                                                        </C.CommentSpoilerOverlay>
                                                    </>
                                                ) : (
                                                    <C.CommentContent>{comment.text}</C.CommentContent>
                                                )}
                                            </C.CommentContentWrap>
                                            <C.CommentInfo>
                                                <C.CommentInfoLeft>
                                                    <C.CommentEpisode>
                                                        <C.CommentTitle>{comment.contentTitle}</C.CommentTitle>
                                                        <div>{comment.episodeNum}화</div>
                                                    </C.CommentEpisode>
                                                    <C.CommentDateBtn>
                                                        <div>{formatDate(comment.createdAt)}</div>
                                                        <C.CommentSpace></C.CommentSpace>
                                                        {comment.isMine ? (
                                                            <>
                                                                <C.CommentLeftBtn onClick={() => handleEditClick(comment)}>수정</C.CommentLeftBtn>
                                                                <C.CommentBtnDivider></C.CommentBtnDivider>
                                                                <C.CommentRightBtn type="button" onClick={() => handleDeleteComment(comment.id)}>삭제</C.CommentRightBtn>
                                                            </>
                                                        ) : (
                                                            <>
                                                                <C.CommentLeftBtn>신고</C.CommentLeftBtn>
                                                            </>
                                                        )}
                                                    </C.CommentDateBtn>  
                                                </C.CommentInfoLeft>
                                                <div>
                                                    {comment.isLiked ? (
                                                        <>
                                                            <C.CommentLikeBtn type="button" onClick={() => handleLikeComment(comment.id)}>
                                                                <C.LikeFullIcon src={LikeFullIcon} />
                                                                <span>{comment.likeCount}</span>
                                                            </C.CommentLikeBtn>
                                                        </>
                                                    ) : (
                                                        <>
                                                            <C.CommentLikeBtn type="button" onClick={() => handleLikeComment(comment.id)}>
                                                                <C.LikeEmptyIcon src={LikeEmptyIcon} />
                                                                <span>{comment.likeCount}</span>
                                                            </C.CommentLikeBtn>
                                                        </>
                                                    )}
                                                </div>
                                            </C.CommentInfo>
                                        </>
                                    )}
                                </C.CommentListLi>
                            ))
                        )}
                    </C.CommentListUl>
                    {pageData && pageData.totalPages > 0 && (
                        <S.PaginationContainer>
                            
                            <S.PaginationIconWrapper
                                onClick={() => handlePageChange(pageData.pageNumber - 1)}
                                disabled={pageData.first}
                            >
                                <PrevIcon />
                            </S.PaginationIconWrapper>

                            <S.PaginationNumberList>
                                
                                {pageNumbers.map((number) => (
                                    <S.PaginationNumberListItem key={number}>
                                        <S.PaginationNumberBtn
                                            $active={pageData.pageNumber === number}
                                            onClick={() => handlePageChange(number)}
                                        >
                                            {number + 1}
                                        </S.PaginationNumberBtn>
                                    </S.PaginationNumberListItem>
                                ))}
                            </S.PaginationNumberList>

                            
                            <S.PaginationIconWrapper
                                onClick={() => handlePageChange(pageData.pageNumber + 1)}
                                disabled={pageData.last}
                            >
                                <NextIcon />
                            </S.PaginationIconWrapper>
                        </S.PaginationContainer>
                    )}
                </C.CommentList>
            </S.ViewerCommentSection>
        </S.Viewer>
    )
}

export default EpisodeCommentsPage;