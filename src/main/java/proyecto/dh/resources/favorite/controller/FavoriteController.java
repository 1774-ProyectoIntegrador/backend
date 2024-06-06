package proyecto.dh.resources.favorite.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.favorite.dto.FavoriteDto;
import proyecto.dh.resources.favorite.entity.Favorite;
import proyecto.dh.resources.favorite.service.FavoriteService;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.service.ProductService;
import proyecto.dh.resources.users.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<FavoriteDto>> getFavorites(@AuthenticationPrincipal User user){
       List<Favorite> favorites = favoriteService.getFavoritesByUser(user);
       List<FavoriteDto> favoriteDtos = favorites.stream()
               .map(favorite -> modelMapper.map(favorite, FavoriteDto.class))
               .collect(Collectors.toList());
       return ResponseEntity.ok(favoriteDtos);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<FavoriteDto> addFavorite(@AuthenticationPrincipal User user,@PathVariable Long productId) throws NotFoundException {
        Product product = productService.getProductById(productId);
        if(product == null){
            return ResponseEntity.notFound().build();
        }

        Favorite favorite = favoriteService.addFavorite(user, product);
        FavoriteDto favoriteDto = modelMapper.map(favorite, FavoriteDto.class);
        return ResponseEntity.ok(favoriteDto);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorite(@AuthenticationPrincipal User user,@PathVariable Long productId) throws NotFoundException{
        Product product = productService.getProductById(productId);
        if(product == null){
            return ResponseEntity.notFound().build();
        }

        favoriteService.deleteFavorite(user, product);
        return ResponseEntity.noContent().build();

    }

}
