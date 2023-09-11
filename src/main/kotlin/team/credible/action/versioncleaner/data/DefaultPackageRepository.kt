package team.credible.action.versioncleaner.data

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import team.credible.action.versioncleaner.domain.PackageRepository
import team.credible.action.versioncleaner.model.Package
import kotlin.coroutines.CoroutineContext

internal class DefaultPackageRepository(
    private val packageDataSource: PackageDataSource,
    private val coroutineContext: CoroutineContext,
) : PackageRepository {
    override suspend fun loadOrganizationPackages(
        organization: String,
        packageType: String,
    ): Result<Collection<Package>> {
        return withContext(coroutineContext) {
            packageDataSource.loadOrganizationPackages(organization, packageType)
        }
    }

    override suspend fun deleteOrganizationPackages(
        data: Collection<Package>,
    ): Collection<Result<String>> {
        return withContext(coroutineContext) {
            val jobs = data.map {
                async {
                    packageDataSource.deleteOrganizationPackage(
                        organization = it.owner.login,
                        packageName = it.name,
                        packageType = it.packageType,
                    )
                }
            }
            jobs.awaitAll()
        }
    }
}
