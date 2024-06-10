package proyecto.dh.resources.product.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.favorite.ProductFavoriteDto;
import proyecto.dh.resources.product.entity.ProductFavorite;
import proyecto.dh.resources.product.service.FavoriteService;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.service.ProductService;
import proyecto.dh.resources.users.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<ProductFavoriteDto>> getFavorites(@AuthenticationPrincipal User user){
       List<ProductFavorite> favorites = favoriteService.getFavoritesByUser(user);
       List<ProductFavoriteDto> productFavoriteDtos = favorites.stream()
               .map(favorite -> modelMapper.map(favorite, ProductFavoriteDto.class))
               .collect(Collectors.toList());
       return ResponseEntity.ok(productFavoriteDtos);
    }
    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{productId}")
    public ResponseEntity<ProductFavorite> create(@PathVariable Long productId, @AuthenticationPrincipal User user) throws NotFoundException {
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Product product = productService.getProductById(productId);
        if(product == null){
            throw new NotFoundException("El producto no se encuentra con ese ID" + productId);
        }

        ProductFavorite favorite = new ProductFavorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        ProductFavorite createdProductFavorite = favoriteService.save(favorite);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductFavorite);

    }
    /*
    @PostMapping("/{productId}")
    public ResponseEntity<Favorite> create(@RequestBody Favorite favorite){
        Favorite createdFavorite = favoriteService.save(favorite);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFavorite);
    }
    */

    /*
    @PostMapping("/{productId}")
    @PreAuthorize("isAuthenticated")
    public ResponseEntity<FavoriteDto> addFavorite(@AuthenticationPrincipal User user,@PathVariable("productId") Long productId) throws NotFoundException {
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Product product = productService.getProductById(productId);
        if(product == null){
            return ResponseEntity.notFound().build();
        }

        Favorite favorite = favoriteService.addFavorite(user, product);
        FavoriteDto favoriteDto = modelMapper.map(favorite, FavoriteDto.class);
        return ResponseEntity.ok(favoriteDto);
    }
    */

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
