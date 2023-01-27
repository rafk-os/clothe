package com.example.backend.controllers;

import com.example.backend.model.Cart;
import com.example.backend.model.Item;
import com.example.backend.services.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }



    @Operation(
            operationId = "createItem",
            responses = {
                    @ApiResponse(responseCode = "201", description = "A new item was created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "403", description = "User not authorized to create new item."),
                    @ApiResponse(responseCode = "404", description = "User not found.")
            }
    )
    @PostMapping(value = "/items", produces = { "application/json" }, consumes = { "application/json" })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        Item result = itemService.add(item);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(
            operationId = "getItem",
            responses = {
                    @ApiResponse(responseCode = "200", description = "A item of given id", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "404", description = "Item not found.")
            }
    )
    @GetMapping(value = "/items/{id}", produces = { "application/json" })
    public ResponseEntity<Item> getItem(@PathVariable("id") Integer id) {
        Item result = itemService.find(id);

        return ResponseEntity.ok(result);
    }

    @Operation(
            operationId = "getItems",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All items in database", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
            }
    )
    @GetMapping(value = "/items", produces = { "application/json" })
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> result = itemService.findAll();

        return ResponseEntity.ok(result);
    }

    @Operation(
            operationId = "deleteItem",
            responses = {
                    @ApiResponse(responseCode = "204", description = "An item was deleted"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "403", description = "User not authorized to delete item."),
                    @ApiResponse(responseCode = "404", description = "Item not found.")
            }
    )
    @DeleteMapping(value = "/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") Integer id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            operationId = "addItemToCart",
            responses = {
                    @ApiResponse(responseCode = "200", description = "A item was added"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "404", description = "Item or cart not found.")
            }
    )
    @PostMapping( value = "/items/{id}/cart", produces = { "application/json" } )
    public ResponseEntity<Cart> addItemToCart(@PathVariable("id") Integer id) {
        Cart result = itemService.addToCart(id);

        return ResponseEntity.ok(result);
    }

    @Operation(
            operationId = "removeItemFromCart",
            responses = {
                    @ApiResponse(responseCode = "200", description = "An item was deleted from cart"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "404", description = "Cart or item not found.")
            }
    )
    @DeleteMapping(value = "/items/{id}/cart", produces = { "application/json" })
    public ResponseEntity<Void> removeItemFromCart(@PathVariable("id") Integer id) {
        itemService.removeFromCart(id);
        return ResponseEntity.ok().build();
    }

}
