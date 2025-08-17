package com.matdev.menuservice.Mapper;


import com.matdev.menuservice.domain.model.MenuItem;
import com.matdev.menuservice.dto.MenuDto;
import com.matdev.menuservice.dto.MenuItemDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
    public class MenuMapper {
        private final ModelMapper mapper = new ModelMapper();

        public MenuDto toDto(Menu entity) {
            return mapper.map(entity, MenuDto.class);
        }

        public Menu toEntity(MenuDto dto) {
            return mapper.map(dto, Menu.class);
        }

        public MenuItemDto toDto(MenuItem entity) {
            return mapper.map(entity, MenuItemDto.class);
        }

        public MenuItem toEntity(MenuItemDto dto) {
            return mapper.map(dto, MenuItem.class);
        }
    }

