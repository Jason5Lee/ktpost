// DO NOT EDIT

package me.jason5lee.ktpost

fun routeAll(scope: kotlinx.coroutines.CoroutineScope, router: io.vertx.ext.web.Router, deps: me.jason5lee.ktpost.common.Dependencies) {
  val logger: mu.KLogger = deps.getDependency()

  me.jason5lee.ktpost.getUser.getUserApi(me.jason5lee.ktpost.getUser.Implementation(deps.getDependency(), )).addRoute(router, scope, logger)
  me.jason5lee.ktpost.createPost.createPostApi(me.jason5lee.ktpost.createPost.Implementation(deps.getDependency(), deps.getDependency(), ), deps.getDependency()).addRoute(router, scope, logger)
  me.jason5lee.ktpost.userRegister.userRegisterApi(me.jason5lee.ktpost.userRegister.Implementation(deps.getDependency(), deps.getDependency(), deps.getDependency(), )).addRoute(router, scope, logger)
  me.jason5lee.ktpost.getPost.getPostApi(me.jason5lee.ktpost.getPost.Implementation(deps.getDependency(), )).addRoute(router, scope, logger)
  me.jason5lee.ktpost.adminRegister.adminRegisterApi(me.jason5lee.ktpost.adminRegister.Implementation(deps.getDependency(), deps.getDependency(), deps.getDependency(), )).addRoute(router, scope, logger)
  me.jason5lee.ktpost.getIdentity.getIdentityApi(me.jason5lee.ktpost.getIdentity.Implementation(deps.getDependency(), ), deps.getDependency()).addRoute(router, scope, logger)
  me.jason5lee.ktpost.listPosts.listPostsApi(me.jason5lee.ktpost.listPosts.Implementation(deps.getDependency(), deps.getDependency(), )).addRoute(router, scope, logger)
  me.jason5lee.ktpost.userLogin.userLoginApi(me.jason5lee.ktpost.userLogin.Implementation(deps.getDependency(), ), deps.getDependency()).addRoute(router, scope, logger)
  me.jason5lee.ktpost.listPostsByCreator.listPostsByCreatorApi(me.jason5lee.ktpost.listPostsByCreator.Implementation(deps.getDependency(), )).addRoute(router, scope, logger)
  me.jason5lee.ktpost.editPost.editPostApi(me.jason5lee.ktpost.editPost.Implementation(deps.getDependency(), ), deps.getDependency()).addRoute(router, scope, logger)
  me.jason5lee.ktpost.deletePost.deletePostApi(me.jason5lee.ktpost.deletePost.Implementation(deps.getDependency(), ), deps.getDependency()).addRoute(router, scope, logger)
  me.jason5lee.ktpost.adminLogin.adminLoginApi(me.jason5lee.ktpost.adminLogin.Implementation(deps.getDependency(), ), deps.getDependency()).addRoute(router, scope, logger)
}
