package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.ClienteDto;
import cr.ac.una.wsrestuna.service.ClienteService;
import cr.ac.una.wsrestuna.util.Respuesta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador REST para gestión de clientes
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Path("/ClienteController")
@Tag(name = "Clientes", description = "Operaciones sobre clientes del restaurante")
public class ClienteController {
    
    private static final Logger LOG = Logger.getLogger(ClienteController.class.getName());
    
    @EJB
    ClienteService clienteService;
    
    @GET
    @Path("/clientes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista todos los clientes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de clientes",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ClienteDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getClientes() {
        try {
            Respuesta res = clienteService.obtenerTodos();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<ClienteDto> clientes = (List<ClienteDto>) res.getResultado("Clientes");
            return Response.ok(clientes).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo clientes.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo clientes: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/cliente/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene un cliente por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ClienteDto.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getCliente(@Parameter(description = "ID del cliente", example = "1")
                               @PathParam("id") Long id) {
        try {
            Respuesta res = clienteService.obtenerPorId(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok((ClienteDto) res.getResultado("Cliente")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo cliente.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo cliente: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/cliente/cedula/{cedula}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene un cliente por cédula")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ClienteDto.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getClientePorCedula(@Parameter(description = "Cédula del cliente", example = "1-1234-5678")
                                        @PathParam("cedula") String cedula) {
        try {
            Respuesta res = clienteService.obtenerPorCedula(cedula);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok((ClienteDto) res.getResultado("Cliente")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo cliente por cédula.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo cliente: " + ex.getMessage())
                .build();
        }
    }
    
    @POST
    @Path("/cliente")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Crea un nuevo cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente creado exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ClienteDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response crearCliente(@Parameter(description = "Datos del cliente a crear", required = true)
                                 ClienteDto clienteDto) {
        try {
            Respuesta res = clienteService.crear(clienteDto);
            if (!res.getEstado()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }
            return Response.ok((ClienteDto) res.getResultado("Cliente")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creando cliente.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error creando cliente: " + ex.getMessage())
                .build();
        }
    }
    
    @PUT
    @Path("/cliente")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Actualiza un cliente existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ClienteDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response actualizarCliente(@Parameter(description = "Datos del cliente a actualizar", required = true)
                                      ClienteDto clienteDto) {
        try {
            Respuesta res = clienteService.actualizar(clienteDto);
            if (!res.getEstado()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }
            return Response.ok((ClienteDto) res.getResultado("Cliente")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error actualizando cliente.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error actualizando cliente: " + ex.getMessage())
                .build();
        }
    }
    
    @DELETE
    @Path("/cliente/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Elimina un cliente por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente eliminado exitosamente",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response eliminarCliente(@Parameter(description = "ID del cliente", example = "1")
                                    @PathParam("id") Long id) {
        try {
            Respuesta res = clienteService.eliminar(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok(res).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando cliente.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error eliminando cliente: " + ex.getMessage())
                .build();
        }
    }
}
