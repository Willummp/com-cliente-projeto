package com.cliente.projeto.crudpb.dto;

public record ErroDTO(String timestamp, int status, String error, String message, String path) {
}