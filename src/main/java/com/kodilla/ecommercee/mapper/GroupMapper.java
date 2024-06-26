package com.kodilla.ecommercee.mapper;

import com.kodilla.ecommercee.dto.response.GroupResponse;
import com.kodilla.ecommercee.entity.Group;
import com.kodilla.ecommercee.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMapper {


    public GroupResponse mapToGroupResponse(Group group) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getProductList().stream().map(Product::getId).toList()
        );
    }
    public List<GroupResponse> mapToGroupListResponse(List<Group> groups) {
        return groups.stream()
                .map(this::mapToGroupResponse)
                .toList();
    }
}
