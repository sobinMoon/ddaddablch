package com.donation.ddb.Converter;

import com.donation.ddb.Domain.Post;
import com.donation.ddb.Domain.StudentUser;
import com.donation.ddb.Dto.Request.PostRequestDto;
import com.donation.ddb.Dto.Response.PostResponseDto;
import com.donation.ddb.Repository.projection.PostWithCount;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class PostConverter {
    public static Post toPost(PostRequestDto.JoinDto joinDto, StudentUser user) {
        return Post.builder()
                .pTitle(joinDto.getTitle())
                .pContent(joinDto.getContent())
                .pNft(joinDto.getImagePath())
                .studentUser(user)
                .build();
    }

    public static PostResponseDto.JoinResultDto toJoinResultDto(Post post) {
        return PostResponseDto.JoinResultDto.builder()
                .postId(post.getPId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static PostResponseDto.PreviewDto toPreviewDto(PostWithCount post) {
        return PostResponseDto.PreviewDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .previewContent(post.getPreviewContent().length() > 100
                        ? post.getPreviewContent().substring(0, 100) + "..."
                        : post.getPreviewContent())
                .nft(post.getNft())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .studentUser(StudentUserConverter.toCommentDto(post.getStudentUser()))
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static PostResponseDto.PreviewListDto toPreviewListDto(Page<PostWithCount> postList) {
        List<PostResponseDto.PreviewDto> previewDtoList = postList.stream()
                .map(PostConverter::toPreviewDto)
                .toList();

        return PostResponseDto.PreviewListDto.builder()
                .postList(previewDtoList)
                .listSize(previewDtoList.size())
                .totalPage(postList.getTotalPages())
                .totalElements(postList.getTotalElements())
                .isFirst(postList.isFirst())
                .isLast(postList.isLast())
                .build();
    }

    public static PostResponseDto.DetailDto toDetailDto(PostWithCount post, Boolean isLiked) {
        return PostResponseDto.DetailDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getPreviewContent())
                .nft(post.getNft().replace("C:\\DDADDABLCH\\", "")
                .replace("\\", "/"))
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .liked(isLiked)
                .studentUser(StudentUserConverter.toCommentDto(post.getStudentUser()))
                .createdAt(post.getCreatedAt())
                .build();
    }


//    public static PostResponseDto.PreviewListDto toPreviewListDto(Page<Post> postList) {
//        List<PostResponseDto.PreviewDto> previewDtoList = postList.stream()
//                .map(PostConverter::toPreviewDto)
//                .collect(Collectors.toList());
//    }
}